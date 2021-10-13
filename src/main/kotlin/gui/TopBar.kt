package gui

import javafx.scene.Parent
import tornadofx.Stylesheet.Companion.menu
import tornadofx.View
import tornadofx.item
import tornadofx.menu
import tornadofx.menubar

class TopBar : View() {
    override val root = menubar {
        menu("File") {
            item("Import...")
            item("Export...")
            item("Quit")
        }
        menu("Edit") {
            item("Advanced")
            item("Paste")
            item("Advanced")
        }
    }
}