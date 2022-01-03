package view.component

import controller.EngineController
import javafx.beans.property.*
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.text.FontWeight
import processing.resample.*
import processing.resample.ResampleMethod.*
import processing.resample.ResampleMethod.PointWithZeros
import tornadofx.*

class ResizerTab : Fragment("Resize Image") {
    private val engineController: EngineController by inject()
    private val previewWidth get() = engineController.previewWidth.toInt()
    private val previewHeight get() = engineController.previewHeight.toInt()

    private val widthValidator = mapOf(
        PointWithZeros to { width: Int ->
            if (width % previewWidth != 0)
                "Width must be a multiple of source for this method"
            else null
        }
    )

    private val heightValidator = mapOf(
        PointWithZeros to { height: Int ->
            if (height % previewHeight != 0)
                "Height must be a multiple of source for this method"
            else null
        }
    )

    override val root = scrollpane(fitToWidth = true, fitToHeight = false) {
        vbox {
            val model = DimensionModel(ImgSize(previewWidth, previewHeight))
            val paramsModel = KernelParamsModel(KernelParams())

            label("Resize Image") {
                vboxConstraints {
                    margin = Insets(20.0, 20.0, 10.0, 10.0)
                }
                style {
                    fontWeight = FontWeight.BOLD
                    fontSize = Dimension(20.0, Dimension.LinearUnits.px)
                }
            }

            padding = Insets(20.0, 20.0, 10.0, 10.0)

            val resizerList = values().toList()
            val comboBox = combobox(values = resizerList) {
                itemsProperty().onChange { }
                value = resizerList[0]
            }

            form {
                fieldset("Dimensions") {
                    field("width") {
                        textfield(model.width).validator {
                            if (it.isNullOrBlank()) error("This field is required")
                            else {
                                val message =
                                    widthValidator[comboBox.value]?.let { it1 -> it1(it.toInt()) }
                                if (message != null) error(message) else null
                            }
                        }
                    }
                    field("height") {
                        textfield(model.height).validator {
                            if (it.isNullOrBlank()) error("This field is required")
                            else {
                                val message =
                                    heightValidator[comboBox.value]?.let { it1 -> it1(it.toInt()) }
                                if (message != null) error(message) else null
                            }
                        }
                    }
                    bicubicParamsField(paramsModel) {
                        text(
                            """
                        B + 2C = 1 is recommended.
                        Common Choices:
                        - B: 0, C: Any - Cardinal Splines
                        - B: 0, C: 0.5 - Catmull-Rom Spline, used in GIMP
                        - B: 0, C: 0.75 - Unnamed, used in Adobe Photoshop
                        - B: 1/3, C: 1/3 - Mitchell-Netravali, used in ImageMagick
                        - B: 1, C: 0 - B-spline, used in Paint.net
                        """.trimIndent()
                        )
                        whenVisible {
                            paramsModel.doubleArg1.value = 1.0 / 3
                            paramsModel.doubleArg2.value = 1.0 / 3
                        }
                        visibleWhen {
                            comboBox.selectionModel.selectedItemProperty()
                                .isEqualTo(Bicubic)
                        }
                        managedProperty().bind(visibleProperty())
                    }
                    lanczosParamsField(paramsModel) {
                        whenVisible {
                            paramsModel.intArg1.value = 3
                            paramsModel.boolArg1.value = false
                        }
                        visibleWhen {
                            comboBox.selectionModel.selectedItemProperty()
                                .isEqualTo(Lanczos)
                        }
                        managedProperty().bind(visibleProperty())
                    }
                    lagrangeParamsField(paramsModel) {
                        whenVisible {
                            paramsModel.intArg1.value = 2
                        }
                        visibleWhen {
                            comboBox.selectionModel.selectedItemProperty()
                                .isEqualTo(Lagrange)
                        }
                        managedProperty().bind(visibleProperty())
                    }

                    button("Resize") {
                        enableWhen(model.valid)
                        action {
                            engineController.resample(
                                previewWidth,
                                previewHeight,
                                model.width.value,
                                model.height.value,
                                comboBox.value,
                                paramsModel.getParams(comboBox.value),
                            )
                        }
                    }
                }
            }
        }
    }
}

class ImgSize(width: Int, height: Int) {
    val widthProperty = SimpleIntegerProperty(this, "width", width)
    val heightProperty = SimpleIntegerProperty(this, "height", height)
}

class DimensionModel(dimension: ImgSize) : ItemViewModel<ImgSize>(dimension) {
    val width = bind(ImgSize::widthProperty) as IntegerProperty
    val height = bind(ImgSize::heightProperty) as IntegerProperty
}

fun EventTarget.bicubicParamsField(
    model: KernelParamsModel,
    op: Fieldset.() -> Unit = {},
): Fieldset =
    fieldset("Bicubic Kernel Params") {
        field("Param B (larger blurrier):") {
            textfield(model.doubleArg1).validator {
                if (it.isNullOrBlank()) error("This field is required")
                else if (it.isDouble() && it.toDouble() in .0..1.0) {
                    null
                } else {
                    error("B must be between 0.0 to 1.0 (inclusive)")
                }
            }
        }
        field("Param C (larger more ringing):") {
            textfield(model.doubleArg2).validator {
                if (it.isNullOrBlank()) error("This field is required")
                else if (it.isDouble() && it.toDouble() in .0..1.0) {
                    null
                } else {
                    error("C must be between 0.0 to 1.0 (inclusive)")
                }
            }
        }
    }.also(op)

fun EventTarget.lanczosParamsField(
    model: KernelParamsModel,
    op: Fieldset.() -> Unit = {},
): Fieldset =
    fieldset("Lanczos Kernel Params") {
        field("Lobes (taps) (larger => sharper & more ringing):") {
            textfield(model.intArg1).validator {
                if (it.isNullOrBlank()) error("This field is required")
                else if (it.isInt() && it.toInt() >= 1) {
                    null
                } else {
                    error("taps must be positive integer")
                }
            }
        }
        text(
            """
            3 taps for downscale and 4 taps for upscale are recommended.
            It should be noted that although a sharp result would be produced usually,
            the side-effect of ringing is commonly observed.
            """.trimIndent()
        )
        checkbox("Enable EWA (Better result at a cost of performance)", model.boolArg1)
        text(
            """
            EWA (Elliptical Weighted Averaging): 
                enable for a better result, 
                disable for better performance;
                EWA mode will using more pixels in calculation. 
            """.trimIndent()
        )
    }.also(op)

fun EventTarget.lagrangeParamsField(
    model: KernelParamsModel,
    op: Fieldset.() -> Unit = {},
): Fieldset =
    fieldset("Lagrange Kernel Params") {
        field("Degree:") {
            textfield(model.intArg1).validator {
                if (it.isNullOrBlank()) error("This field is required")
                else if (it.isInt() && it.toInt() >= 0) {
                    null
                } else {
                    error("degree must be a non-positive integer")
                }
            }
        }
        text("(degree + 1) pixels will be involved when calculating in each direction")
    }.also(op)

class KernelParams {
    val doubleArg1Property = SimpleDoubleProperty(this, "doubleArg1", .0)
    val doubleArg2Property = SimpleDoubleProperty(this, "doubleArg2", .0)
    val intArg1Property = SimpleIntegerProperty(this, "intArg1", 0)
    val boolArg1Property = SimpleBooleanProperty(this, "boolArg1", false)
}

class KernelParamsModel(params: KernelParams) : ItemViewModel<KernelParams>(params) {
    val doubleArg1 = bind(KernelParams::doubleArg1Property) as DoubleProperty
    val doubleArg2 = bind(KernelParams::doubleArg2Property) as DoubleProperty
    val intArg1 = bind(KernelParams::intArg1Property) as IntegerProperty
    val boolArg1 = bind(KernelParams::boolArg1Property) as BooleanProperty

    fun getParams(method: ResampleMethod): Params? = when (method) {
        Point -> null
        PointWithZeros -> null
        Bilinear -> null
        Bicubic -> BicubicParams(doubleArg1.value, doubleArg2.value)
        Lanczos -> LanczosParams(intArg1.value, boolArg1.value)
        Lagrange -> LagrangeParams(intArg1.value)
    }
}
