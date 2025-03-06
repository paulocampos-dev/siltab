package com.prototype.silver_tab.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.prototype.silver_tab.SilverTabApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Language {
    ENGLISH,
    PORTUGUESE,
    CHINESE
}

object LocalizationManager {
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        // Start collecting the saved language
        scope.launch {
            SilverTabApplication.languagePreferences.language.collect { language ->
                _currentLanguage.value = language
            }
        }
    }

    fun setLanguage(language: Language) {
        scope.launch {
            SilverTabApplication.languagePreferences.saveLanguage(language.name)
        }
    }
}

data class StringResources(
    // Camera and Gallery
    val camera: String,
    val gallery: String,
    val noImageSelected: String,
    val noImageFound: String,
    val loadingImages: String,
    val selectImageSource: String,
    val selectImageSourceDescription: String,
    val extraImages: String,

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

    // Profile Modal
    val profileTitle: String,
    val profileEmail: String,
    val profileUsername: String,
    val profileRole: String,
    val profilePosition: String,
    val profileEntity: String,
    val profileAccess: String,

    // Vehicle Types
    val vehicleTypeHybrid: String,
    val vehicleTypeElectric: String,

    // PDI Screen
    val startPdi: String,
    val searchCars: String,
    val finishPdi: String,
    val cancel: String,
    val pdiTitle: String,
    val selectDealer: String,
    val dealerCode: String,
    val region: String,
    val status: String,
    val searchDealers: String,
    val informationAboutLastPdi: String,
    val Vin: String = "VIN",

    // Check Screen
    val vehicleInfo: String,
    val chassisNumber: String,
    val chassisPhoto: String,
    val socPercentage: String,
    val batteryPhoto: String,
    val batteryVoltage: String,
    val voltagePhoto: String,
    val tirePressure: String,
    val frontLeft: String,
    val frontRight: String,
    val rearLeft: String,
    val rearRight: String,
    val tirePressurePhoto: String,
    val carStarted: String,
    val carStartedPhoto: String,
    val additionalInfo: String,
    val carStartedQuestion: String,

    // Confirmation Dialogs
    val cancelConfirmation: String,
    val cancelConfirmationMessage: String,
    val yes: String,
    val no: String,
    val finishConfirmation: String,
    val finishConfirmationMessage: String,
    val changePdi: String,
    val newPdi: String,
    val pdiDetailsTitle: String,
    val lastUpdate: String,

    // Help texts
    val helpTitle: String,
    val chassisHelp: String,
    val socHelp: String,
    val tireHelp: String,
    val understood: String,
    val hybridHelp: String,
    val battery12vHelp: String,

    // Common
    val close: String,
    val confirm: String,
    val error: String,
    val loading: String,
    val sendingData: String,
    val neededField: String,
    val errorTitle: String,
    val fillRequiredFields: String,

    val selectDealerRequired: String,
    val selectDealerRequiredDesc: String

)


val englishStrings = StringResources(
    // Camera and Gallery
    camera = "Camera",
    gallery = "Gallery",
    noImageSelected = "No image selected",
    noImageFound = "No image found",
    loadingImages = "Loading images...",
    selectImageSource = "Select Image Source",
    selectImageSourceDescription = "Choose how you want to add a image",
    extraImages = "You can add extra images",

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
    loading = "Loading...",

    // Profile Modal
    profileTitle = "Profile",
    profileEmail = "Email",
    profileUsername = "Username",
    profileRole = "Role",
    profilePosition = "Position",
    profileEntity = "Entity Authority",
    profileAccess = "Commercial Policy Access",
    informationAboutLastPdi = "Information about the last PDI from this car",

    // Vehicle Types
    vehicleTypeHybrid = "Hybrid",
    vehicleTypeElectric = "Electric",

    // PDI Screen
    pdiTitle = "Which car model will you PDI?",
    selectDealer = "Select Dealer",
    dealerCode = "Code",
    region = "Region",
    status = "Status",
    searchDealers = "Search dealers...",


    // Check Screen
    vehicleInfo = "Vehicle Information",
    chassisNumber = "Vehicle Chassis",
    chassisPhoto = "Chassis Photo",
    socPercentage = "Measured SOC Percentage",
    batteryPhoto = "Battery Photo",
    batteryVoltage = "12V Battery Voltage",
    voltagePhoto = "Voltage Photo",
    tirePressure = "Tire Pressure",
    frontLeft = "FL",
    frontRight = "FR",
    rearLeft = "RL",
    rearRight = "RR",
    tirePressurePhoto = "Tire Pressure Photo",
    carStarted = "Was the car started for at least 5 minutes?",
    carStartedPhoto = "Car Started Photo",
    additionalInfo = "Is there any additional information?",
    carStartedQuestion = "Was the car started for 5 minutes?",

    // Confirmation Dialogs
    cancelConfirmation = "Cancel?",
    cancelConfirmationMessage = "Are you sure you want to cancel? All data filled so far will be lost.",
    yes = "Yes, cancel",
    no = "No, go back",
    finishConfirmation = "Complete?",
    finishConfirmationMessage = "The PDI process will be closed and you won't be able to change the information later. Are you sure you want to complete?",
    changePdi = "Change PDI",
    newPdi = "New PDI",
    pdiDetailsTitle = "Last PDI Details",
    lastUpdate = "Last update",

    // Help texts
    helpTitle = "Help",
    chassisHelp = "View chassis on vehicle front glass as shown in the image:",
    socHelp = "View SOC on vehicle panel as shown in the image:",
    tireHelp = "View tire pressure on vehicle panel as shown in the image:",
    understood = "Understood!",
    hybridHelp = "Press the brake and press the Start/Stop button located next to the vehicle's dashboard.",
    battery12vHelp = "With a multimeter, do the verification of the voltage through the negative and positive poles as shown in the image.  NOTE: Make sure to measure the battery with the negative pole disconnected",
    sendingData = "Sending data...",
    neededField = "Required field",
    errorTitle = "Validation Error",
    fillRequiredFields = "Please, fill required fields",

    selectDealerRequired = "Select a dealer",
    selectDealerRequiredDesc = "Please select a dealer before continuing",

)

val portugueseStrings = StringResources(
    // Camera and Gallery
    camera = "Câmera",
    gallery = "Galeria",
    noImageSelected = "Não há imagem selecionada",
    noImageFound = "Nenhuma imagem encontrada",
    loadingImages = "Carregando imagens...",
    selectImageSource = "Escolha a fonte da imagem",
    selectImageSourceDescription = "Escolha como você quer adicionar uma imagem",
    extraImages = "Você pode colocar fotos extras",

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
    loading = "Carregando...",

    // Profile Modal
    profileTitle = "Perfil",
    profileEmail = "Email",
    profileUsername = "Nome de Usuário",
    profileRole = "Função",
    profilePosition = "Cargo",
    profileEntity = "Autoridade da Entidade",
    profileAccess = "Acesso à Política Comercial",

    // Vehicle Types
    vehicleTypeHybrid = "Híbrido",
    vehicleTypeElectric = "Elétrico",

    // PDI Screen
    pdiTitle = "Qual o modelo do carro que você fará o PDI?",
    selectDealer = "Selecionar Concessionária",
    dealerCode = "Código",
    region = "Região",
    status = "Status",
    searchDealers = "Buscar concessionárias...",
    informationAboutLastPdi = "Informações sobre o último PDI deste veículo",

    // Check Screen
    vehicleInfo = "Informações do Veículo",
    chassisNumber = "Chassi do veículo",
    chassisPhoto = "Foto do Chassi",
    socPercentage = "Percentual do SOC medido",
    batteryPhoto = "Foto da Bateria",
    batteryVoltage = "Tensão da bateria 12V",
    voltagePhoto = "Foto da Tensão",
    tirePressure = "Pressão dos Pneus",
    frontLeft = "DE",
    frontRight = "DD",
    rearLeft = "TE",
    rearRight = "TD",
    tirePressurePhoto = "Foto da Pressão dos Pneus",
    carStarted = "Carro Ligado",
    carStartedPhoto = "Foto do Carro Ligado",
    additionalInfo = "Há alguma informação adicional?",
    carStartedQuestion = "O carro foi ligado por 5 minutos?",

    // Confirmation Dialogs
    cancelConfirmation = "Cancelar?",
    cancelConfirmationMessage = "Tem certeza que deseja cancelar? Todos os dados preenchidos até agora serão perdidos.",
    yes = "Sim, cancelar",
    no = "Não, voltar",
    finishConfirmation = "Concluir?",
    finishConfirmationMessage = "O processo PDI será encerrado e você não poderá alterar as informações depois. Tem certeza que quer concluir?",
    changePdi = "Alterar PDI",
    newPdi = "Novo PDI",
    pdiDetailsTitle = "Detalhes do Último PDI",
    lastUpdate = "Última atualização",

    // Help texts
    helpTitle = "Ajuda",
    chassisHelp = "Visualizar chassi no vidro frontal do veículo conforme a imagem:",
    socHelp = "Visualizar SOC no painel do veículo conforme a imagem:",
    tireHelp = "Visualizar dos pneus no painel do veículo conforme a imagem:",
    understood = "Entendi!",
    hybridHelp = "Apertar o freio e acionar botar Start/Stop localizado próximo ao painel do veículo",
    battery12vHelp = "Com multímetro, faça a verificação da voltagem através dos pólos negativo e positivo conforme imagem e identificar o carro.  OBS: Certifique de medir a bateria com o pólo negativo desconectado",

    // helper text
    sendingData = "Enviando dados...",
    neededField = "Campo necessário",
    errorTitle = "Erro de preenchimento",
    fillRequiredFields = "Por favor, preencha os campos necessários",

    selectDealerRequired = "Por favor selecione uma concessionária",
    selectDealerRequiredDesc = "Por favor, selecione uma concessionária antes de continuar",

    )

val chineseStrings = StringResources(
    // Camera and Gallery
    camera = "相机",
    gallery = "图库",
    noImageSelected = "未选择图片",
    noImageFound = "未找到图片",
    loadingImages = "加载中...",
    selectImageSource = "TODO",
    selectImageSourceDescription = "TODO",
    extraImages = "TODO",


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
    loading = "加载中...",

    // Profile Modal
    profileTitle = "个人资料",
    profileEmail = "电子邮件",
    profileUsername = "用户名",
    profileRole = "角色",
    profilePosition = "职位",
    profileEntity = "实体权限",
    profileAccess = "商业政策访问权限",

    // Vehicle Types
    vehicleTypeHybrid = "混合动力",
    vehicleTypeElectric = "纯电动",

    // PDI Screen
    pdiTitle = "您要进行PDI的车型是什么？",
    selectDealer = "选择经销商",
    dealerCode = "代码",
    region = "地区",
    status = "状态",
    searchDealers = "搜索经销商...",
    informationAboutLastPdi = "TODO",

    // Check Screen
    vehicleInfo = "车辆信息",
    chassisNumber = "车辆底盘号",
    chassisPhoto = "底盘照片",
    socPercentage = "测量SOC百分比",
    batteryPhoto = "电池照片",
    batteryVoltage = "12V电池电压",
    voltagePhoto = "电压照片",
    tirePressure = "轮胎压力",
    frontLeft = "左前",
    frontRight = "右前",
    rearLeft = "左后",
    rearRight = "右后",
    tirePressurePhoto = "轮胎压力照片",
    carStarted = "车辆启动",
    carStartedPhoto = "车辆启动照片",
    additionalInfo = "是否有其他信息？",
    carStartedQuestion = "车辆是否启动5分钟？",

    // Confirmation Dialogs
    cancelConfirmation = "取消？",
    cancelConfirmationMessage = "确定要取消吗？到目前为止填写的所有数据都将丢失。",
    yes = "是的，取消",
    no = "不，返回",
    finishConfirmation = "完成？",
    finishConfirmationMessage = "PDI流程将关闭，之后您将无法更改信息。确定要完成吗？",
    changePdi = "更改PDI",
    newPdi = "新PDI",
    pdiDetailsTitle = "最后PDI详情",
    lastUpdate = "最后更新",

    // Help texts
    helpTitle = "帮助",
    chassisHelp = "如图所示查看车辆前挡风玻璃上的底盘号：",
    socHelp = "如图所示查看车辆仪表板上的SOC：",
    tireHelp = "如图所示查看车辆仪表板上的轮胎压力：",
    understood = "明白了！",
    hybridHelp = "踩下制动器，然后按下车辆仪表板旁边的启动/停止按钮。",
    battery12vHelp = "TEXT TO BE ADDED",
    sendingData = "TEXT TO BE ADDED",
    neededField = "TEXT TO BE ADDED",
    errorTitle = "TEXT TO BE ADDED",
    fillRequiredFields = "TEXT TO BE ADDED",

    selectDealerRequired = "TO BE ADDED",
    selectDealerRequiredDesc = "TO BE ADDED",

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