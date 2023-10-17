package com.rdev.tt.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rdev.tt.ui.revision.RevisionScreen
import com.rdev.tt.ui.suite.SuiteScreen
import kotlinx.coroutines.launch

object HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeViewModel>()

        val state by viewModel.uiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        if (state !is HomeUiState.Content) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            return
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HomeDrawerComp()
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Driving Theory Test") },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(Icons.Filled.Menu, null)
                            }
                        }
                    )
                }
            ) { innerPadding ->
                if (state !is HomeUiState.Content) {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    return@Scaffold
                }

                (state as? HomeUiState.Content)?.let { content ->
                    HomeComp(
                        content = content,
                        openSuite = { suite -> navigator.push(SuiteScreen(suite)) },
                        reviewWronglyAnsweredQuestions = {
                            coroutineScope.launch {
                                val questions = viewModel.getWronglyAnsweredQuestions()
                                navigator.push(RevisionScreen(questions))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(innerPadding)
                    )
                }
            }
        }
    }
}