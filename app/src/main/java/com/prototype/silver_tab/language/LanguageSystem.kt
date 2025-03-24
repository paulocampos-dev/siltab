package com.prototype.silver_tab.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.repository.StringResourceRepository
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
fun LocalizationProvider(
    content: @Composable () -> Unit
) {
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
    val no: String = "No",
    val success : String = "Success",
    val ok : String = "Ok",

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
    val sortNewestFirst: String = "Newest First",
    val sortOldestFirst: String = "Oldest First",

    // Inspection Details
    val informationAboutLastPdi: String = "Information About Last PDI",
    val vin: String = "VIN",
    val socPercentage: String = "SOC %",
    val tirePressure: String = "Tire Pressure",
    val battery12v: String = "12V Battery",
    val noImageFound: String = "No image found",
    val loadingImages: String = "Loading images...",
    val unknownError: String = "Unknown Error",
    val lastUpdate: String = "Last Update",
    val chooseCarModel: String = "Choose Car Model",
    val wrongInfo: String = "Wrong Information",
    val wrongInfoTitle: String = "What information is wrong?",
    val wrongInfoDescription: String = "Please select what information needs to be corrected.",
    val markAsSoldTitle: String = "Mark Vehicle as Sold",
    val markAsSoldDescription: String = "Are you sure you want to mark this vehicle as sold?",
    val selectSaleDate: String = "Yes, Select Sale Date",
    val markAsSold: String = "Mark as Sold",
    val newPdi: String = "New PDI",
    val pdiInformation: String = "PDI Information",
    val vinNumber: String = "VIN Number",
    val vinUpdatedSuccessfully: String = "VIN Updated Successfully",
    val noImageData: String = "No Image Data",

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

    // CheckScreen - Images
    val camera: String = "Camera",
    val gallery: String = "Gallery",
    val noImageSelected : String = "No Image Selected",
    val selectImageSource: String = "Select Image Source",
    val selectImageSourceDescription: String = "Choose how you want to add an image",
    val successPdiUpdated: String = "PDI Updated Successfully",
    val pdiUpdateSuccess: String = "The PDI has been successfully updated.",
    val updatePdi: String = "Update PDI",

    // CheckScreen - Inspection
    val newInspection: String = "New PDI from a unregistered car",
    val updateInspection: String = "Update Inspection",
    val enterSocPercentage: String = "Enter SOC %",
    val socPercentageRange: String = "Range: 0-100%",
    val enterBatteryVoltage: String = "Enter Battery Voltage",
    val batteryVoltageRange: String = "Range: 0-12V",
    val vinPhotos: String = "VIN Photos",
    val socPhotos: String = "SOC Photos",
    val batteryPhotos: String = "Battery Photos",
    val tirePhotos: String = "Tire Pressure Photos",
    val additionalPhotos: String = "Additional Photos",
    val hybridCarCheck: String = "Hybrid Car Check",
    val fiveMinutesHybridCheck: String = "Car started for 5 minutes",
    val comments: String = "Comments",
    val commentsOptional: String = "Comments (Optional)",
    val psi: String = "PSI",
    val frontLeft: String = "Front Left Tire",
    val frontRight: String = "Front Right Tire",
    val rearLeft: String = "Rear Left Tire",
    val rearRight: String = "Rear Right Tire",
    val savePdi: String = "Save Inspection",
    val saveNewCarPdi: String = "Save New Car PDI",
    val pdiSavedSuccessfully: String = "Inspection saved successfully",
    val cancelConfirmation : String = "Yes, Cancel",
    val cancelConfirmationMessage : String = "Are you sure you want to cancel? All entered data will be lost.",
    val finishPdi : String = "Finish PDI",
    val finishConfirmationMessage : String = "Are you sure you want to finish this PDI? Make sure all the information is correct.",
    val finishConfirmation : String = "Yes, Finish",
    val successPDI : String = "PDI Completed Successfully",
    val successExtra : String = "The PDI has been successfully recorded.",
    val vinCannotBeChanged: String = "VIN cannot be changed",
    val correctionMode: String = "Correction Mode",
    val loadingData: String = "Loading data...",
    val savingData: String = "Saving data...",
    val vinCantBeChangedCorrection : String = "VIN cannot be changed in correction mode",

    // CheckScreenError Messages
    val vinCannotBeEmpty : String = "VIN cannot be empty",
    val invalidVinFormat : String = "Invalid VIN format",

    val socMustBeZeroToHundred : String = "SOC must be between 0 and 100%",
    val socMustBeValidNumber : String = "SOC must be a valid number",

    val batteryVoltageMustBeZeroToFifteen : String = "12V Battery voltage must be between 0 and 15V",
    val batteryVoltageMustBeValidNumber : String = "12V Battery voltage must be a valid number",

    val tirePressureMustBeBetweenZeroAndFifty : String = "Tire pressure must be between 0 and 50 PSI",
    val tirePressureMustBeValidNumber : String = "Tire pressure must be a valid number",


    val duplicateVin : String = "Duplicate VIN",
    val duplicateVinMessage : String = "This VIN is already registered in the system. Would you like to find it in the inspection history?",

    // Help system strings
    val helpTitle: String = "Help for",
    val understood: String = "I understand",
    val chassisHelp: String = "The Vehicle Identification Number (VIN) is a 17-character code that uniquely identifies your vehicle. It can be found on the driver's side dashboard, door jamb, or in vehicle registration documents.",
    val tireHelp: String = "Check tire pressure when tires are cold. The recommended pressure for this vehicle is usually between 32-36 PSI, but check your vehicle's specifications for exact values.",
    val hybridHelp: String = "For hybrid vehicles, it's important to run the engine for at least 5 minutes to ensure proper system checks and to maintain the 12V battery charge.",
    val battery12vHelp: String = "The 12V battery voltage should be between 12.4V-12.7V when the vehicle is off, and 13.7V-14.7V when the engine is running.",
    val socHelp: String = "State of Charge (SOC) represents the current charge level of the main battery as a percentage of its total capacity. This should be recorded exactly as shown on the vehicle's display.",
    val commentsHelp: String = "If you found anything worth mentioning in the inspection, please let us know.",
    val requiredForHybrid: String = "Required for Hybrid Vehicles"
)



private fun getPortugueseStrings(): StringResources {
    return StringResources(
        // Common
        email = "Email",
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
        close = "Fechar",
        no = "Não",
        success = "Sucesso",
        ok = "Ok",

        // Language Selection
        selectLanguage = "Selecionar Idioma",
        english = "Inglês",
        portuguese = "Português",
        chinese = "Chinês",

        // Login
        username = "Nome de usuário",
        password = "Senha",
        login = "Entrar",
        loginError = "Falha no login. Por favor, verifique suas credenciais.",

        // Dealer Selection
        dealerCode = "Código da Concessionária",
        region = "Região",
        status = "Status",
        selectDealer = "Selecionar Concessionária",
        searchDealers = "Buscar concessionárias",
        noDealersAvailable = "Nenhuma concessionária disponível",
        errorLoadingDealers = "Erro ao carregar concessionárias",
        selectDealerToSeeInspections = "Selecione uma concessionária para ver inspeções",
        selectDealerRequired = "Selecione uma concessionária",
        selectDealerRequiredDesc = "Selecione uma concessionária para continuar",

        // Inspection List
        inspections = "Inspeções",
        noInspections = "Nenhuma inspeção encontrada ou nenhuma concessionária selecionada",
        noMatchingInspections = "Nenhuma inspeção correspondente encontrada",
        startNewInspection = "Iniciar Nova Inspeção",
        searchForCarOrVin = "Buscar por modelo ou VIN",
        searchCars = "Buscar carros...",
        sortNewestFirst = "Mais recentes primeiro",
        sortOldestFirst = "Mais antigos primeiro",

        // Inspection Details
        informationAboutLastPdi = "Informações sobre o Último PDI",
        vin = "Chassi",
        socPercentage = "Porcentagem do SOC",
        tirePressure = "Pressão dos Pneus",
        battery12v = "Bateria 12V",
        noImageFound = "Nenhuma imagem encontrada",
        loadingImages = "Carregando imagens...",
        unknownError = "Erro Desconhecido",
        lastUpdate = "Última Atualização",
        chooseCarModel = "Escolher Modelo do Carro",
        wrongInfo = "Informação Incorreta",
        wrongInfoTitle = "Qual informação está errada?",
        wrongInfoDescription = "Por favor, selecione qual informação precisa ser corrigida.",
        markAsSoldTitle = "Marcar Veículo como Vendido",
        markAsSoldDescription = "Tem certeza de que deseja marcar este veículo como vendido?",
        selectSaleDate = "Sim, Selecionar Data de Venda",
        markAsSold = "Marcar como Vendido",
        newPdi = "Novo PDI",
        pdiInformation = "Informações do PDI",
        vinNumber = "Número do Chassi",
        vinUpdatedSuccessfully = "Chassi Atualizado com Sucesso",
        noImageData = "Sem Dados de Imagem",

        // Vehicle Types
        vehicleTypeHybrid = "Híbrido",
        vehicleTypeElectric = "Elétrico",

        // Time expressions
        today = "Hoje",
        yesterday = "Ontem",
        daysAgo = "dias atrás",

        // Profile Modal
        profileTitle = "Perfil",
        profileEmail = "Email",
        profileUsername = "Nome de Usuário",
        profileRole = "Função",
        profilePosition = "Cargo",
        profileEntity = "Entidade",

        // CheckScreen - Images
        camera = "Câmera",
        gallery = "Galeria",
        noImageSelected = "Nenhuma Imagem Selecionada",
        selectImageSource = "Selecionar Fonte da Imagem",
        selectImageSourceDescription = "Escolha como você deseja adicionar uma imagem",
        successPdiUpdated = "PDI Atualizado com Sucesso",
        pdiUpdateSuccess = "O PDI foi atualizado com sucesso.",
        updatePdi = "Atualizar PDI",

        // CheckScreen - Inspection
        newInspection = "Novo PDI",
        updateInspection = "Atualizar Inspeção",
        enterSocPercentage = "Digite a % do SOC",
        socPercentageRange = "Intervalo: 0-100%",
        enterBatteryVoltage = "Digite a Voltagem da Bateria",
        batteryVoltageRange = "Intervalo: 0-12V",
        vinPhotos = "Fotos do Chassi",
        socPhotos = "Fotos do SOC",
        batteryPhotos = "Fotos da Bateria",
        tirePhotos = "Fotos da Pressão dos Pneus",
        additionalPhotos = "Fotos Adicionais",
        hybridCarCheck = "Verificação de Carro Híbrido",
        fiveMinutesHybridCheck = "Carro ligado por 5 minutos",
        comments = "Comentários",
        commentsOptional = "Comentários (Opcional)",
        psi = "PSI",
        frontLeft = "Dianteiro Esquerdo",
        frontRight = "Dianteiro Direito",
        rearLeft = "Traseiro Esquerdo",
        rearRight = "Traseiro Direito",
        savePdi = "Salvar Inspeção",
        pdiSavedSuccessfully = "Inspeção salva com sucesso",
        cancelConfirmation = "Sim, Cancelar",
        cancelConfirmationMessage = "Tem certeza de que deseja cancelar? Todos os dados inseridos serão perdidos.",
        finishPdi = "Finalizar PDI",
        finishConfirmationMessage = "Tem certeza de que deseja finalizar este PDI? Certifique-se de que todas as informações estão corretas.",
        finishConfirmation = "Sim, Finalizar",
        successPDI = "PDI Concluído com Sucesso",
        successExtra = "O PDI foi registrado com sucesso.",
        vinCannotBeChanged = "O Chassi não pode ser alterado",
        correctionMode = "Modo de Correção",
        loadingData = "Carregando dados...",
        saveNewCarPdi = "Salvar PDI de um novo carro",
        savingData = "Salvando dados...",
        vinCantBeChangedCorrection = "O Chassi não pode ser alterado no modo de correção",

        duplicateVin = "Chassi Duplicado",
        duplicateVinMessage = "Este Chassi já está registrado no sistema. Deseja encontrá-lo no histórico de inspeções?",

        // Help system strings
        helpTitle = "Ajuda para",
        understood = "Eu entendo",
        chassisHelp = "O Número de Identificação do Veículo (VIN/Chassi) é um código de 17 caracteres que identifica exclusivamente seu veículo. Pode ser encontrado no painel do lado do motorista, na batente da porta ou nos documentos de registro do veículo.",
        tireHelp = "Verifique a pressão dos pneus quando os pneus estiverem frios. A pressão recomendada para este veículo normalmente está entre 32-36 PSI, mas verifique as especificações do seu veículo para valores exatos.",
        hybridHelp = "Para veículos híbridos, é importante manter o motor funcionando por pelo menos 5 minutos para garantir verificações adequadas do sistema e para manter a carga da bateria de 12V.",
        battery12vHelp = "A tensão da bateria de 12V deve estar entre 12,4V-12,7V quando o veículo estiver desligado, e 13,7V-14,7V quando o motor estiver em funcionamento.",
        socHelp = "O Estado de Carga (SOC) representa o nível de carga atual da bateria principal como uma porcentagem de sua capacidade total. Isso deve ser registrado exatamente como mostrado no display do veículo.",
        commentsHelp = "Se você encontrou alguma informação que precisa ser mencionada, por favor, avise.",
        requiredForHybrid = "Requerido para Veículos Híbridos"
    )
}

private fun getChineseStrings(): StringResources {
    return StringResources(
        // Common
        email = "电子邮箱",
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
        close = "关闭",
        no = "否",
        success = "成功",
        ok = "确定",

        // Language Selection
        selectLanguage = "选择语言",
        english = "英语",
        portuguese = "葡萄牙语",
        chinese = "中文",

        // Login
        username = "用户名",
        password = "密码",
        login = "登录",
        loginError = "登录失败。请检查您的凭据。",

        // Dealer Selection
        dealerCode = "经销商代码",
        region = "地区",
        status = "状态",
        selectDealer = "选择经销商",
        searchDealers = "搜索经销商",
        noDealersAvailable = "没有可用的经销商",
        errorLoadingDealers = "加载经销商时出错",
        selectDealerToSeeInspections = "请选择经销商查看检查",
        selectDealerRequired = "需要选择经销商",
        selectDealerRequiredDesc = "选择经销商以继续",

        // Inspection List
        inspections = "检查",
        noInspections = "未找到检查或未选择经销商",
        noMatchingInspections = "未找到匹配的检查",
        startNewInspection = "开始新检查",
        searchForCarOrVin = "按车型或VIN搜索",
        searchCars = "搜索车辆...",
        sortNewestFirst = "最新优先",
        sortOldestFirst = "最旧优先",

        // Inspection Details
        informationAboutLastPdi = "最后PDI信息",
        vin = "车架号",
        socPercentage = "电量百分比",
        tirePressure = "胎压",
        battery12v = "12V电池",
        noImageFound = "未找到图片",
        loadingImages = "加载图片中...",
        unknownError = "未知错误",
        lastUpdate = "最后更新",
        chooseCarModel = "选择车型",
        wrongInfo = "错误信息",
        wrongInfoTitle = "哪些信息有误？",
        wrongInfoDescription = "请选择需要更正的信息。",
        markAsSoldTitle = "标记车辆为已售出",
        markAsSoldDescription = "确定要将此车辆标记为已售出吗？",
        selectSaleDate = "是的，选择销售日期",
        markAsSold = "标记为已售出",
        newPdi = "新PDI",
        pdiInformation = "PDI信息",
        vinNumber = "车架号码",
        vinUpdatedSuccessfully = "车架号更新成功",
        noImageData = "无图像数据",

        // Vehicle Types
        vehicleTypeHybrid = "混合动力",
        vehicleTypeElectric = "电动",

        // Time expressions
        today = "今天",
        yesterday = "昨天",
        daysAgo = "天前",

        // Profile Modal
        profileTitle = "个人资料",
        profileEmail = "电子邮箱",
        profileUsername = "用户名",
        profileRole = "角色",
        profilePosition = "职位",
        profileEntity = "实体",

        // CheckScreen - Images
        camera = "相机",
        gallery = "图库",
        noImageSelected = "未选择图片",
        selectImageSource = "选择图片来源",
        selectImageSourceDescription = "选择如何添加图片",
        successPdiUpdated = "PDI更新成功",
        pdiUpdateSuccess = "PDI已成功更新。",
        updatePdi = "更新PDI",

        // CheckScreen - Inspection
        newInspection = "未注册车辆的新PDI",
        updateInspection = "更新检查",
        enterSocPercentage = "输入电量百分比",
        socPercentageRange = "范围: 0-100%",
        enterBatteryVoltage = "输入电池电压",
        batteryVoltageRange = "范围: 0-12V",
        vinPhotos = "车架号照片",
        socPhotos = "电量照片",
        batteryPhotos = "电池照片",
        tirePhotos = "轮胎压力照片",
        additionalPhotos = "额外照片",
        hybridCarCheck = "混合动力车检查",
        fiveMinutesHybridCheck = "车辆已启动5分钟",
        comments = "评论",
        commentsOptional = "评论 (可选)",
        psi = "PSI",
        frontLeft = "左前",
        frontRight = "右前",
        rearLeft = "左后",
        rearRight = "右后",
        savePdi = "保存检查",
        pdiSavedSuccessfully = "检查保存成功",
        cancelConfirmation = "是的，取消",
        cancelConfirmationMessage = "确定要取消吗？所有输入的数据将丢失。",
        finishPdi = "完成PDI",
        finishConfirmationMessage = "确定要完成此PDI吗？请确保所有信息正确无误。",
        finishConfirmation = "是的，完成",
        successPDI = "PDI成功完成",
        successExtra = "PDI已成功记录。",
        vinCannotBeChanged = "车架号不能更改",
        correctionMode = "更正模式",
        loadingData = "加载数据中...",
        savingData = "保存数据中...",
        vinCantBeChangedCorrection = "在更正模式下车架号不能更改",

        duplicateVin = "重复车架号",
        duplicateVinMessage = "该车架号已在系统中注册。您想在检查历史中查找它吗？",

        // Help system strings
        helpTitle = "帮助",
        understood = "我明白了",
        chassisHelp = "车辆识别号（VIN）是一个17位代码，唯一标识您的车辆。它可以在驾驶员侧仪表板、车门边框或车辆注册文件中找到。",
        tireHelp = "当轮胎冷却时检查胎压。此车辆的推荐压力通常在32-36 PSI之间，但请查看您车辆的规格以获取确切值。",
        hybridHelp = "对于混合动力车辆，让发动机运行至少5分钟非常重要，以确保正确的系统检查并维持12V电池充电。",
        battery12vHelp = "12V电池电压在车辆关闭时应在12.4V-12.7V之间，发动机运行时应在13.7V-14.7V之间。",
        socHelp = "充电状态（SOC）表示主电池当前充电水平占其总容量的百分比。这应该与车辆显示屏上显示的完全一致。"
    )
}
