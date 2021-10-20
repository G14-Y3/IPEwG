package controller

import models.EngineModel
import tornadofx.Controller

class FileController : Controller() {

    private val engine: EngineModel by inject()

    fun load(path: String) = engine.load(path)

    fun save(path: String, format: String) = engine.save(path, format)

    fun undo() = engine.undo()

    fun redo() = engine.redo()

    fun revert() = engine.revert()
}