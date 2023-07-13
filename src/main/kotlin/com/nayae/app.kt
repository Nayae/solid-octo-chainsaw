package com.nayae

import com.nayae.component.grid.GridService
import com.nayae.component.grid.GridView
import com.nayae.gfx.Application2D


object Application : Application2D() {

    private val service = GridService()
    private val view = GridView(service)

    override fun beforeRun() {
        view.initialize()
    }

    override fun onUpdate() {
        service.update()
    }

    override fun onRender() {
        view.render()
    }

    override fun afterRun() {
        view.dispose()
    }
}

fun main() {
    Application2D.launch(Application)
}

