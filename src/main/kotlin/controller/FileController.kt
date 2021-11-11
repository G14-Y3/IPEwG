package controller

import models.EngineModel
import tornadofx.*

class FileController : Controller() {

    private val engine: EngineModel by inject()

    fun loadImage(path: String) = engine.load(path)

    fun saveImage(path: String, format: String, mode: String = "") = engine.save(path, format, mode)

    fun loadJson(path: String) = engine.loadJson(path)

    fun saveJson(path: String) = engine.saveJson(path)

    fun loadEncodeImage(path: String) = engine.loadEncodeImage(path)

    fun undo() = engine.undo()

    fun redo() = engine.redo()

    fun revert() = engine.revert()
}