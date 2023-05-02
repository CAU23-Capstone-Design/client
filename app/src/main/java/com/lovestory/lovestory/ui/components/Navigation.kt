package com.lovestory.lovestory.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lovestory.lovestory.graphs.MainScreens

@Composable
fun BottomNaviagtionBar(navHostController: NavHostController){
    val screens = listOf(
        MainScreens.DashBoard,
        MainScreens.Gallery,
        MainScreens.Calendar,
        MainScreens.Profile
    )
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        Row (
            modifier = Modifier
                .padding(start = 0.dp, end = 0.dp, top = 8.dp, bottom = 0.dp)
                .background(Color(0xFFF3F3F3))
                .fillMaxWidth()
                .height(64.dp)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        Color(0xFFF6F6F6),
                        Offset(0f, 0f),
                        Offset(size.width, 0f),
                        strokeWidth
                    )
                }
            ,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ){
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navHostController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: MainScreens,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    val background =
        if (selected) Color(0xFFB6B6) else Color.Transparent

    val contentColor =
        if (selected) Color.Black else Color.Black

    val borderModifier = if (selected) Modifier.border(2.dp, Color.Black, CircleShape) else Modifier

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(CircleShape)
            .background(background)
            .then(borderModifier)
            .clickable {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            },
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = screen.icon),
                    contentDescription = "icon",
                    tint = contentColor
                )
            }
            AnimatedVisibility(visible = selected) {
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = screen.title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
