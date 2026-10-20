package com.example.monitoreosilos.ui.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.monitoreosilos.conectividad.ConectividadObserver
import com.example.monitoreosilos.notificaciones.NotificationHelper
import com.example.monitoreosilos.ui.screen.AjustesScreen
import com.example.monitoreosilos.ui.screen.AlertasScreen
import com.example.monitoreosilos.ui.screen.CamaraGaleriaScreen
import com.example.monitoreosilos.ui.screen.ContactoScreen
import com.example.monitoreosilos.ui.screen.HomeScreen
import com.example.monitoreosilos.ui.screen.PerfilScreen
import com.example.monitoreosilos.ui.screen.RegistroScreen
import com.example.monitoreosilos.ui.screen.ResumenScreen
import com.example.monitoreosilos.ui.screen.SilosScreen
import com.example.monitoreosilos.ui.viewmodel.AjustesViewModel
import com.example.monitoreosilos.ui.viewmodel.CamaraGaleriaViewModel
import com.example.monitoreosilos.ui.viewmodel.MainViewModel
import com.example.monitoreosilos.ui.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

// Describe cada destino una sola vez; lo usan la NavigationBar y el Drawer
private data class Destino(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
)

private val destinosBarra = listOf(
    Destino(AppRoutes.HOME, "Inicio", Icons.Default.Home),
    Destino(AppRoutes.SILOS, "Silos", Icons.Default.Storage),
    Destino(AppRoutes.ALERTAS, "Alertas", Icons.Default.Warning),
    Destino(AppRoutes.PERFIL, "Perfil", Icons.Default.Person)
)

// El Drawer incluye además Evidencia, Ajustes, Registro y Contacto
private val destinosDrawer = destinosBarra + listOf(
    Destino(AppRoutes.EVIDENCIA, "Evidencia", Icons.Default.PhotoCamera),
    Destino(AppRoutes.AJUSTES, "Ajustes", Icons.Default.Settings),
    Destino(AppRoutes.REGISTRO, "Registro", Icons.Default.PersonAdd),
    Destino(AppRoutes.CONTACTO, "Contacto", Icons.Default.Phone)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    // ViewModel compartido entre Registro y Resumen (mismo alcance: la Activity)
    val usuarioViewModel: UsuarioViewModel = viewModel()

    // ViewModel de preferencias (DataStore), compartido por Ajustes y Alertas
    val ajustesViewModel: AjustesViewModel = viewModel()

    // ViewModel de la evidencia fotográfica (cámara y galería)
    val camaraViewModel: CamaraGaleriaViewModel = viewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // --- Notificaciones: permiso (Android 13+) y aviso cuando un silo pasa a crítico ---
    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* si se rechaza, simplemente no se muestran avisos */ }

    LaunchedEffect(Unit) {
        if (!NotificationHelper.tienePermiso(context)) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Se observa siempre el estado de Ajustes para respetar el switch "Alertas activadas"
    val ajustes by ajustesViewModel.uiState.collectAsStateWithLifecycle()
    val alertasActivadas by rememberUpdatedState(ajustes.alertasActivadas)

    LaunchedEffect(Unit) {
        viewModel.eventosCriticos.collect { silo ->
            if (alertasActivadas) {
                NotificationHelper.mostrarAlertaCritica(context, silo)
            }
        }
    }

    // --- Conectividad: true si hay red, false si se perdió ---
    val conectado by remember { ConectividadObserver(context).conectado }
        .collectAsStateWithLifecycle(initialValue = true)

    // El título cambia según la ruta actual
    val titulo = when (currentRoute) {
        AppRoutes.RESUMEN -> "Resumen"
        else -> destinosDrawer.find { it.ruta == currentRoute }?.titulo
            ?: "Monitoreo de Silos"
    }

    fun irA(ruta: String) {
        navController.navigate(ruta) {
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Monitoreo de Silos",
                    modifier = Modifier.padding(24.dp)
                )
                destinosDrawer.forEach { destino ->
                    NavigationDrawerItem(
                        selected = currentRoute == destino.ruta,
                        onClick = {
                            irA(destino.ruta)
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(destino.icono, contentDescription = destino.titulo)
                        },
                        label = { Text(destino.titulo) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    destinosBarra.forEach { destino ->
                        NavigationBarItem(
                            selected = currentRoute == destino.ruta,
                            onClick = { irA(destino.ruta) },
                            icon = {
                                Icon(destino.icono, contentDescription = destino.titulo)
                            },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                if (!conectado) {
                    Text(
                        text = "Sin conexión a internet · mostrando últimos datos disponibles",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp)
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.HOME,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(AppRoutes.HOME) {
                        HomeScreen(
                            viewModel = viewModel,
                            onVerSilosClick = { irA(AppRoutes.SILOS) }
                        )
                    }
                    composable(AppRoutes.SILOS) { SilosScreen(viewModel) }
                    composable(AppRoutes.ALERTAS) {
                        AlertasScreen(
                            viewModel = viewModel,
                            ajustesViewModel = ajustesViewModel
                        )
                    }
                    composable(AppRoutes.PERFIL) {
                        PerfilScreen(
                            viewModel = viewModel,
                            onEditarClick = { irA(AppRoutes.REGISTRO) }
                        )
                    }
                    composable(AppRoutes.CONTACTO) { ContactoScreen() }
                    composable(AppRoutes.REGISTRO) {
                        RegistroScreen(
                            viewModel = usuarioViewModel,
                            onRegistrado = { navController.navigate(AppRoutes.RESUMEN) }
                        )
                    }
                    composable(AppRoutes.RESUMEN) {
                        ResumenScreen(
                            viewModel = usuarioViewModel,
                            onEditarClick = { navController.popBackStack() },
                            onInicioClick = { irA(AppRoutes.HOME) }
                        )
                    }
                    composable(AppRoutes.AJUSTES) { AjustesScreen(ajustesViewModel) }
                    composable(AppRoutes.EVIDENCIA) {
                        CamaraGaleriaScreen(
                            viewModel = camaraViewModel,
                            mainViewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}