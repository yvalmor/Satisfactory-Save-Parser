package parser

private val conveyorBeltNames = arrayOf(
    "/Game/FactoryGame/Buildable/Factory/ConveyorBeltMk1/Build_ConveyorBeltMk1.Build_ConveyorBeltMk1_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorBeltMk2/Build_ConveyorBeltMk2.Build_ConveyorBeltMk2_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorBeltMk3/Build_ConveyorBeltMk3.Build_ConveyorBeltMk3_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorBeltMk4/Build_ConveyorBeltMk4.Build_ConveyorBeltMk4_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorBeltMk5/Build_ConveyorBeltMk5.Build_ConveyorBeltMk5_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorBeltMk6/Build_ConveyorBeltMk6.Build_ConveyorBeltMk6_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorLiftMk1/Build_ConveyorLiftMk1.Build_ConveyorLiftMk1_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorLiftMk2/Build_ConveyorLiftMk2.Build_ConveyorLiftMk2_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorLiftMk3/Build_ConveyorLiftMk3.Build_ConveyorLiftMk3_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorLiftMk4/Build_ConveyorLiftMk4.Build_ConveyorLiftMk4_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorLiftMk5/Build_ConveyorLiftMk5.Build_ConveyorLiftMk5_C",
    "/Game/FactoryGame/Buildable/Factory/ConveyorLiftMk6/Build_ConveyorLiftMk6.Build_ConveyorLiftMk6_C",
)

private val gameModeNames = arrayOf(
    "/Game/FactoryGame/-Shared/Blueprint/BP_GameMode.BP_GameMode_C",
    "/Game/FactoryGame/-Shared/Blueprint/BP_GameState.BP_GameState_C",
)

private val powerLineNames = arrayOf(
    "/Game/FactoryGame/Buildable/Factory/PowerLine/Build_PowerLine.Build_PowerLine_C",
    "/Game/FactoryGame/Events/Christmas/Buildings/PowerLineLights/Build_XmassLightsLine.Build_XmassLightsLine_C",
)

private val vehicleNames = arrayOf(
    "/Game/FactoryGame/Buildable/Vehicle/Cyberwagon/Testa_BP_WB.Testa_BP_WB_C",
    "/Game/FactoryGame/Buildable/Vehicle/Explorer/BP_Explorer.BP_Explorer_C",
    "/Game/FactoryGame/Buildable/Vehicle/Golfcart/BP_Golfcart.BP_Golfcart_C",
    "/Game/FactoryGame/Buildable/Vehicle/Tractor/BP_Tractor.BP_Tractor_C",
    "/Game/FactoryGame/Buildable/Vehicle/Truck/BP_Truck.BP_Truck_C",
)

private val conveyorChainActor = arrayOf(
    "/Script/FactoryGame.FGConveyorChainActor",
    "/Script/FactoryGame.FGConveyorChainActor_RepSizeNoCull",
    "/Script/FactoryGame.FGConveyorChainActor_RepSizeMedium",
    "/Script/FactoryGame.FGConveyorChainActor_RepSizeLarge",
    "/Script/FactoryGame.FGConveyorChainActor_RepSizeHuge",
)

private val FGComponentNames = arrayOf(
    "/Script/FactoryGame.FGDroneMovementComponent",
    "/Script/FactoryGame.FGFactoryConnectionComponent",
    "/Script/FactoryGame.FGFactoryLegsComponent",
    "/Script/FactoryGame.FGHealthComponent",
    "/Script/FactoryGame.FGInventoryComponent",
    "/Script/FactoryGame.FGInventoryComponentEquipment",
    "/Script/FactoryGame.FGInventoryComponentTrash",
    "/Script/FactoryGame.FGPipeConnectionComponent",
    "/Script/FactoryGame.FGPipeConnectionComponentHyper",
    "/Script/FactoryGame.FGPipeConnectionFactory",
    "/Script/FactoryGame.FGPowerConnectionComponent",
    "/Script/FactoryGame.FGPowerInfoComponent",
    "/Script/FactoryGame.FGRailroadTrackConnectionComponent",
    "/Script/FactoryGame.FGShoppingListComponent",
    "/Script/FactoryGame.FGTrainPlatformConnection",
)

private const val playerStateName = "/Game/FactoryGame/Character/Player/BP_PlayerState.BP_PlayerState_C"
private const val circuitSubsystemName = "/Game/FactoryGame/-Shared/Blueprint/BP_CircuitSubsystem.BP_CircuitSubsystem_C"
private const val lightweightBuildableSubsystem = "/Script/FactoryGame.FGLightweightBuildableSubsystem"

fun isObjectConveyorBelt(objectName: String) = objectName in conveyorBeltNames
fun isObjectGameMode(objectName: String) = objectName in gameModeNames
fun isObjectPowerLine(objectName: String) = objectName in powerLineNames
fun isObjectVehicle(objectName: String) = objectName in vehicleNames
fun isObjectConveyorChainActor(objectName: String) = objectName in conveyorChainActor
fun isObjectFGComponent(objectName: String) = objectName in FGComponentNames
fun isObjectPlayerState(objectName: String) = objectName == playerStateName
fun isObjectCircuitSubsystem(objectName: String) = objectName == circuitSubsystemName
fun isObjectLightweightBuildableSubsystem(objectName: String) = objectName == lightweightBuildableSubsystem
