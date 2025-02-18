package com.prototype.silver_tab.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Language {
    ENGLISH,
    PORTUGUESE,
    CHINESE
}

object LocalizationManager {
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    fun setLanguage(language: Language) {
        _currentLanguage.value = language
    }
}

data class StringResources(
    // Login Screen
    val email: String,
    val password: String,
    val login: String,
    val loginError: String,
    val selectLanguage: String,

    // Welcome Screen
    val welcome: String,
    val welcomeUserPrefix: String,
    val startInspection: String,
    val readyToStart: String,

    // PDI Screen
    val startPdi: String,
    val searchCars: String,
    val finishPdi: String,
    val cancel: String,

    // Common
    val close: String,
    val confirm: String,
    val error: String,
    val loading: String
)

val englishStrings = StringResources(
    email = "Email",
    password = "Password",
    login = "Log in",
    loginError = "Login failed",
    selectLanguage = "Select Language",
    welcome = "Welcome",
    readyToStart = "Ready to start?",
    welcomeUserPrefix = "Welcome, ",
    startInspection = "Start Inspection",
    startPdi = "Start PDI",
    searchCars = "Search cars...",
    finishPdi = "Finish",
    cancel = "Cancel",
    close = "Close",
    confirm = "Confirm",
    error = "Error",
    loading = "Loading..."
)

val portugueseStrings = StringResources(
    email = "Email",
    password = "Senha",
    login = "Entrar",
    loginError = "Falha no login",
    selectLanguage = "Selecionar Idioma",
    welcome = "Bem-vindo",
    readyToStart = "Pronto para começar?",
    welcomeUserPrefix = "Bem-vindo, ",
    startInspection = "Iniciar Inspeção",
    startPdi = "Iniciar PDI",
    searchCars = "Pesquisar carros...",
    finishPdi = "Finalizar",
    cancel = "Cancelar",
    close = "Fechar",
    confirm = "Confirmar",
    error = "Erro",
    loading = "Carregando..."
)

val chineseStrings = StringResources(
    email = "电子邮件",
    password = "密码",
    login = "登录",
    loginError = "登录失败",
    selectLanguage = "选择语言",
    welcome = "欢迎",
    readyToStart = "准备开始？",
    welcomeUserPrefix = "欢迎, ",
    startInspection = "开始检查",
    startPdi = "开始PDI",
    searchCars = "搜索汽车...",
    finishPdi = "完成",
    cancel = "取消",
    close = "关闭",
    confirm = "确认",
    error = "错误",
    loading = "加载中..."
)

@Composable
fun LocalizationProvider(content: @Composable () -> Unit) {
    val currentLanguage by LocalizationManager.currentLanguage.collectAsState()

    val strings = when (currentLanguage) {
        Language.ENGLISH -> englishStrings
        Language.PORTUGUESE -> portugueseStrings
        Language.CHINESE -> chineseStrings
    }

    CompositionLocalProvider(LocalStringResources provides strings) {
        content()
    }
}

val LocalStringResources = staticCompositionLocalOf<StringResources> { englishStrings }