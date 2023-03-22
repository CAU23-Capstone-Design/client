package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lovestory.lovestory.R
import com.lovestory.lovestory.ui.screens.Screen

@Composable
fun BottomNavigation(navController: NavHostController){
    val items = listOf<BottomNavItem>(
        BottomNavItem.Home,
        BottomNavItem.Gallery,
        BottomNavItem.Calendar,
        BottomNavItem.Option
    )

    androidx.compose.material.BottomNavigation(backgroundColor = Color.White, contentColor = Color(0xFFFFB6B6))
    {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.title),
                        modifier = Modifier
                            .width(26.dp)
                            .height(26.dp)
                    )
                },
                label = { Text(stringResource(id = item.title), fontSize = 9.sp) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.Gray,
                selected = currentRoute == item.screenRoute,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem(
    val title: Int, val icon: Int, val screenRoute: String
){
    object Home : BottomNavItem(R.string.text_home, R.drawable.ic_home, HOME)
    object Gallery : BottomNavItem(R.string.text_gallery, R.drawable.ic_gallery, GALLERY)
    object Calendar : BottomNavItem(R.string.text_calendar, R.drawable.ic_calendaer, CALENDAR)
    object Option : BottomNavItem(R.string.text_option, R.drawable.ic_option, OPTION)
}

const val HOME = "HOME"
const val GALLERY = "GALLERY"
const val CALENDAR = "CALENDAR"
const val OPTION = "OPTION"