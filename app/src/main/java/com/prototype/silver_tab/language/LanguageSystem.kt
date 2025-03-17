package com.prototype.silver_tab.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.prototype.silver_tab.SilverTabApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


enum class Language {
    ENGLISH,
    PORTUGUESE,
    CHINESE
}


object LocalizationManager {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    init {
        loadSavedLanguage()
    }

    private fun loadSavedLanguage() {
        scope.launch {
            try {
                val preferences = SilverTabApplication.languagePreferences
                val languageString = withContext(Dispatchers.IO) {
                    preferences.language.first()
                }

                _currentLanguage.value = try {
                    Language.valueOf(languageString.name)
                } catch (e: Exception) {
                    Timber.e("Invalid language: $languageString, defaulting to ENGLISH")
                    Language.ENGLISH
                }

                Timber.d("Loaded language preference: ${_currentLanguage.value}")
            } catch (e: Exception) {
                Timber.e("Error loading language preference: ${e.message}")
            }
        }
    }

    fun setLanguage(language: Language) {
        scope.launch {
            try {
                _currentLanguage.value = language
                SilverTabApplication.languagePreferences.saveLanguage(language.name)
                Timber.d("Language changed to: $language")
            } catch (e: Exception) {
                Timber.e("Error saving language preference: ${e.message}")
            }
        }
    }
}

@Composable
fun LocalizationProvider(content: @Composable () -> Unit) {
    val languagePreferences = remember { SilverTabApplication.languagePreferences }

    val languageFlow = languagePreferences.language.map { languageString ->
        try {
            Language.valueOf(languageString.name)
        } catch (e: Exception) {
            Language.ENGLISH
        }
    }

    val language by languageFlow.collectAsState(initial = Language.ENGLISH)

    val stringResources = remember(language) {
        when (language) {
            Language.PORTUGUESE -> getPortugueseStrings()
            Language.CHINESE -> getChineseStrings()
            else -> StringResources() // Default English
        }
    }

    CompositionLocalProvider(LocalStringResources provides stringResources) {
        content()
    }
}

val LocalStringResources = compositionLocalOf { StringResources() }

data class StringResources(
    // Common
    val email: String = "Email",
    val loading: String = "Loading...",
    val error: String = "Error",
    val cancel: String = "Cancel",
    val save: String = "Save",
    val confirm: String = "Confirm",
    val next: String = "Next",
    val back: String = "Back",
    val finish: String = "Finish",
    val search: String = "Search",
    val refreshData: String = "Refresh Data",
    val close: String = "Close",

    // Language Selection
    val selectLanguage: String = "Select Language",
    val english: String = "English",
    val portuguese: String = "Portuguese",
    val chinese: String = "Chinese",

    // Login
    val username: String = "Username",
    val password: String = "Password",
    val login: String = "Login",
    val loginError: String = "Login failed. Please check your credentials.",

    // Dealer Selection
    val dealerCode: String = "Dealer Code",
    val region: String = "Region",
    val status: String = "Status",
    val selectDealer: String = "Select Dealer",
    val searchDealers: String = "Search for dealers",
    val noDealersAvailable: String = "No dealers available",
    val errorLoadingDealers: String = "Error loading dealers",
    val selectDealerToSeeInspections: String = "Please select a dealer to see inspections",
    val selectDealerRequired: String = "Select a dealer",
    val selectDealerRequiredDesc: String = "Select a dealer to continue",

    // Inspection List
    val inspections: String = "Inspections",
    val noInspections: String = "No inspections found or no dealer selected",
    val noMatchingInspections: String = "No matching inspections found",
    val startNewInspection: String = "Start New Inspection",
    val searchForCarOrVin: String = "Search by car model or VIN",
    val searchCars: String = "Search cars...",

    // Inspection Details
    val informationAboutLastPdi: String = "Information About Last PDI",
    val vin: String = "VIN",
    val socPercentage: String = "SOC %",
    val tirePressure: String = "Tire Pressure",
    val battery12v: String = "12V Battery",
    val noImageFound: String = "No image found",
    val loadingImages: String = "Loading images...",

    val chooseCarModel: String = "Choose Car Model",

    // Vehicle Types
    val vehicleTypeHybrid: String = "Hybrid",
    val vehicleTypeElectric: String = "Electric",

    // Time expressions
    val today: String = "Today",
    val yesterday: String = "Yesterday",
    val daysAgo: String = "days ago",

    // Profile Modal
    val profileTitle: String = "Profile",
    val profileEmail: String = "Email",
    val profileUsername: String = "Username",
    val profileRole: String = "Role",
    val profilePosition: String = "Position",
    val profileEntity: String = "Entity",
)


/**
 * Create Portuguese string resources
 */
private fun getPortugueseStrings(): StringResources {
    return StringResources(
        loading = "Carregando...",
        error = "Erro",
        cancel = "Cancelar",
        save = "Salvar",
        confirm = "Confirmar",
        next = "Próximo",
        back = "Voltar",
        finish = "Finalizar",
        search = "Buscar",
        refreshData = "Atualizar Dados",

        username = "Nome de usuário",
        password = "Senha",
        login = "Entrar",
        loginError = "Falha no login. Por favor, verifique suas credenciais.",

        selectDealer = "Selecionar Concessionária",
        noDealersAvailable = "Nenhuma concessionária disponível",
        errorLoadingDealers = "Erro ao carregar concessionárias",
        selectDealerToSeeInspections = "Selecione uma concessionária para ver inspeções",

        inspections = "Inspeções",
        noInspections = "Nenhuma inspeção encontrada ou nenhuma concessionária selecionada",
        noMatchingInspections = "Nenhuma inspeção correspondente encontrada",
        startNewInspection = "Iniciar Nova Inspeção",
        searchForCarOrVin = "Buscar por modelo ou VIN",

        informationAboutLastPdi = "Informações sobre o Último PDI",
        vin = "Chassi",
        socPercentage = "Porcentagem do SOC",
        tirePressure = "Pressão dos Pneus",
        battery12v = "Bateria 12V",
        noImageFound = "Nenhuma imagem encontrada",
        loadingImages = "Carregando imagens...",

        vehicleTypeHybrid = "Híbrido",
        vehicleTypeElectric = "Elétrico",

        today = "Hoje",
        yesterday = "Ontem",
        daysAgo = "dias atrás"
    )
}

/**
 * Create Chinese string resources
 */
private fun getChineseStrings(): StringResources {
    return StringResources(
        loading = "加载中...",
        error = "错误",
        cancel = "取消",
        save = "保存",
        confirm = "确认",
        next = "下一步",
        back = "返回",
        finish = "完成",
        search = "搜索",
        refreshData = "刷新数据",

        username = "用户名",
        password = "密码",
        login = "登录",
        loginError = "登录失败。请检查您的凭据。",

        selectDealer = "选择经销商",
        noDealersAvailable = "没有可用的经销商",
        errorLoadingDealers = "加载经销商时出错",
        selectDealerToSeeInspections = "请选择经销商查看检查",

        inspections = "检查",
        noInspections = "未找到检查",
        noMatchingInspections = "未找到匹配的检查",
        startNewInspection = "开始新检查",
        searchForCarOrVin = "按车型或VIN搜索",

        informationAboutLastPdi = "最后PDI信息",
        vin = "车架号",
        socPercentage = "电量百分比",
        tirePressure = "胎压",
        battery12v = "12V电池",
        noImageFound = "未找到图片",
        loadingImages = "加载图片中...",

        vehicleTypeHybrid = "混合动力",
        vehicleTypeElectric = "电动",

        today = "今天",
        yesterday = "昨天",
        daysAgo = "天前"
    )
}