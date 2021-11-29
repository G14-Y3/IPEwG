package view.fragment

import controller.FileController
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import models.EngineModel
import tornadofx.*

class TransformationList : Fragment() {

    private val engine: EngineModel by inject()
    private val fileController: FileController by inject()

    override val root = vbox {
        alignment = Pos.CENTER
        label("Transformations") {
            vboxConstraints {
                margin = Insets(10.0, 20.0, 10.0, 10.0)
            }
            style {
                fontWeight = FontWeight.BOLD
                fontSize =
                    Dimension(15.0, Dimension.LinearUnits.px)
            }
        }

        hbox {
            alignment = Pos.CENTER
            padding = Insets(0.0, 10.0, 0.0, 10.0)

            listview(engine.transformations) {
                prefWidth = 400.0
                selectionModel.selectedIndexProperty()
                    .onChange {
                        engine.setCurrentIndex(it)
                    }
                engine.updateListSelection =
                    { selectionModel.select(engine.currIndex) }
            }
        }
        hbox {
            alignment = Pos.CENTER
            buttonbar {
                padding = Insets(20.0, 10.0, 20.0, 10.0)
                button("Undo").setOnAction { fileController.undo() }
                button("Redo").setOnAction { fileController.redo() }
                button("Revert").setOnAction { fileController.revert() }
            }
        }
    }
}