package com.lovestory.lovestory.model

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import android.graphics.Canvas
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.AbstractComposeView
import androidx.core.graphics.applyCanvas
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import com.google.maps.android.compose.ComposeUiViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.google.maps.android.compose.InputHandler
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.currentCameraPositionState
import com.google.maps.android.compose.rememberComposeUiViewRenderer
import com.google.maps.android.compose.rememberReattachClickListenersHandle
import kotlinx.coroutines.launch

@Composable
@GoogleMapComposable
@MapsComposeExperimentalApi
public fun <T : ClusterItem> Clustering(
    items: Collection<T>,
    onClusterClick: (Cluster<T>) -> Boolean = { false },
    onClusterItemClick: (T) -> Boolean = { false },
    onClusterItemInfoWindowClick: (T) -> Unit = { },
    onClusterItemInfoWindowLongClick: (T) -> Unit = { },
    clusterContent: @[UiComposable Composable] ((Cluster<T>) -> Unit)? = null,
    clusterItemContent: @[UiComposable Composable] ((T) -> Unit)? = null,
) {
    val clusterManager = rememberClusterManager(clusterContent, clusterItemContent) ?: return

    ResetMapListeners(clusterManager)
    SideEffect {
        clusterManager.setOnClusterClickListener(onClusterClick)
        clusterManager.setOnClusterItemClickListener(onClusterItemClick)
        clusterManager.setOnClusterItemInfoWindowClickListener(onClusterItemInfoWindowClick)
        clusterManager.setOnClusterItemInfoWindowLongClickListener(onClusterItemInfoWindowLongClick)
    }
    InputHandler(
        onMarkerClick = clusterManager::onMarkerClick,
        onInfoWindowClick = clusterManager::onInfoWindowClick,
        onInfoWindowLongClick = clusterManager.markerManager::onInfoWindowLongClick,
        onMarkerDrag = clusterManager.markerManager::onMarkerDrag,
        onMarkerDragEnd = clusterManager.markerManager::onMarkerDragEnd,
        onMarkerDragStart = clusterManager.markerManager::onMarkerDragStart,
    )
    val cameraPositionState = currentCameraPositionState
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (!isMoving) {
                    clusterManager.onCameraIdle()
                }
            }
    }
    val itemsState = rememberUpdatedState(items)
    LaunchedEffect(itemsState) {
        snapshotFlow { itemsState.value.toList() }
            .collect { items ->
                clusterManager.clearItems()
                clusterManager.addItems(items)
                clusterManager.cluster()
            }
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun <T : ClusterItem> rememberClusterManager(
    clusterContent: @Composable ((Cluster<T>) -> Unit)?,
    clusterItemContent: @Composable ((T) -> Unit)?,
): ClusterManager<T>? {
    val clusterContentState = rememberUpdatedState(clusterContent)
    val clusterItemContentState = rememberUpdatedState(clusterItemContent)
    val context = LocalContext.current
    val viewRendererState = rememberUpdatedState(rememberComposeUiViewRenderer())
    val clusterManagerState: MutableState<ClusterManager<T>?> = remember { mutableStateOf(null) }
    MapEffect(context) { map ->
        val clusterManager = ClusterManager<T>(context, map)

        launch {
            snapshotFlow {
                clusterContentState.value != null || clusterItemContentState.value != null
            }
                .collect { hasCustomContent ->
                    val renderer = if (hasCustomContent) {
                        ComposeUiClusterRenderer(
                            context,
                            scope = this,
                            map,
                            clusterManager,
                            viewRendererState,
                            clusterContentState,
                            clusterItemContentState,
                        )
                    } else {
                        DefaultClusterRenderer(context, map, clusterManager)
                    }
                    clusterManager.renderer = renderer
                }
        }

        clusterManagerState.value = clusterManager
    }
    return clusterManagerState.value
}

@Composable
private fun ResetMapListeners(
    clusterManager: ClusterManager<*>,
) {
    val reattach = rememberReattachClickListenersHandle()
    LaunchedEffect(clusterManager, reattach) {
        Handler(Looper.getMainLooper()).post {
            reattach()
        }
    }
}

internal class ComposeUiClusterRenderer<T : ClusterItem>(
    private val context: Context,
    private val scope: CoroutineScope,
    map: GoogleMap,
    clusterManager: ClusterManager<T>,
    private val viewRendererState: State<ComposeUiViewRenderer>,
    private val clusterContentState: State<@Composable ((Cluster<T>) -> Unit)?>,
    private val clusterItemContentState: State<@Composable ((T) -> Unit)?>,
) : DefaultClusterRenderer<T>(
    context,
    map,
    clusterManager
) {

    private val fakeCanvas = Canvas()
    private val keysToViews = mutableMapOf<ViewKey<T>, ViewInfo>()

    override fun onClustersChanged(clusters: Set<Cluster<T>>) {
        super.onClustersChanged(clusters)
        val keys = clusters.flatMap { it.computeViewKeys() }

        with(keysToViews.iterator()) {
            forEach { (key, viewInfo) ->
                if (key !in keys) {
                    remove()
                    viewInfo.onRemove()
                }
            }
        }
        keys.forEach { key ->
            if (key !in keysToViews.keys) {
                createAndAddView(key)
            }
        }
    }

    private fun Cluster<T>.computeViewKeys(): Set<ViewKey<T>> {
        return if (shouldRenderAsCluster(this)) {
            setOf(ViewKey.Cluster(this))
        } else {
            items.mapTo(mutableSetOf()) { ViewKey.Item(it) }
        }
    }

    private fun createAndAddView(key: ViewKey<T>): ViewInfo {
        val view = InvalidatingComposeView(
            context,
            content = when (key) {
                is ViewKey.Cluster -> {
                    { clusterContentState.value?.invoke(key.cluster) }
                }

                is ViewKey.Item -> {
                    { clusterItemContentState.value?.invoke(key.item) }
                }
            }
        )
        val renderHandle = viewRendererState.value.startRenderingView(view)
        val rerenderJob = scope.launch {
            collectInvalidationsAndRerender(key, view)
        }

        val viewInfo = ViewInfo(
            view,
            onRemove = {
                rerenderJob.cancel()
                renderHandle.dispose()
            },
        )
        keysToViews[key] = viewInfo
        return viewInfo
    }

    private suspend fun collectInvalidationsAndRerender(
        key: ViewKey<T>,
        view: InvalidatingComposeView
    ) {
        callbackFlow {
            // When invalidated, emit on the next frame
            var invalidated = false
            view.onInvalidate = {
                if (!invalidated) {
                    launch {
                        awaitFrame()
                        trySend(Unit)
                        invalidated = false
                    }
                    invalidated = true
                }
            }
            view.doOnAttach {
                view.doOnDetach { close() }
            }
            awaitClose()
        }
            .collectLatest {
                when (key) {
                    is ViewKey.Cluster -> getMarker(key.cluster)
                    is ViewKey.Item -> getMarker(key.item)
                }?.setIcon(renderViewToBitmapDescriptor(view))
            }

    }

    override fun getDescriptorForCluster(cluster: Cluster<T>): BitmapDescriptor {
        return if (clusterContentState.value != null) {
            val viewInfo = keysToViews.entries
                .firstOrNull { (key, _) -> (key as? ViewKey.Cluster)?.cluster == cluster }
                ?.value
                ?: createAndAddView(cluster.computeViewKeys().first())
            renderViewToBitmapDescriptor(viewInfo.view)
        } else {
            super.getDescriptorForCluster(cluster)
        }
    }

    override fun onBeforeClusterItemRendered(item: T, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)

        if (clusterItemContentState.value != null) {
            val viewInfo = keysToViews.entries
                .firstOrNull { (key, _) -> (key as? ViewKey.Item)?.item == item }
                ?.value
                ?: createAndAddView(ViewKey.Item(item))
            markerOptions.icon(renderViewToBitmapDescriptor(viewInfo.view))
        }
    }

    private fun renderViewToBitmapDescriptor(view: AbstractComposeView): BitmapDescriptor {
        /* AndroidComposeView triggers LayoutNode's layout phase in the View draw phase,
           so trigger a draw to an empty canvas to force that */
        view.draw(fakeCanvas)
        val viewParent = (view.parent as ViewGroup)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(viewParent.width, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(viewParent.height, View.MeasureSpec.AT_MOST),
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        bitmap.applyCanvas {
            view.draw(this)
        }

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private sealed class ViewKey<T : ClusterItem> {
        data class Cluster<T : ClusterItem>(
            val cluster: com.google.maps.android.clustering.Cluster<T>
        ) : ViewKey<T>()

        data class Item<T : ClusterItem>(
            val item: T
        ) : ViewKey<T>()
    }

    private class ViewInfo(
        val view: AbstractComposeView,
        val onRemove: () -> Unit,
    )

    /**
     * An [AbstractComposeView] that calls [onInvalidate] whenever the Compose render layer is
     * invalidated. Works by reporting invalidations from its inner AndroidComposeView.
     */
    private class InvalidatingComposeView(
        context: Context,
        private val content: @Composable () -> Unit,
    ) : AbstractComposeView(context) {

        var onInvalidate: (() -> Unit)? = null

        @Composable
        override fun Content() = content()

        override fun onDescendantInvalidated(child: View, target: View) {
            super.onDescendantInvalidated(child, target)
            onInvalidate?.invoke()
        }
    }
}