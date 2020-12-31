package com.binarysushi.studio.projectWizard

import com.binarysushi.studio.StudioBundle.message
import com.binarysushi.studio.StudioIcons
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import javax.swing.Icon

class StudioModuleType :
    ModuleType<StudioModuleBuilder>(ID) {
    override fun createModuleBuilder(): StudioModuleBuilder {
        return StudioModuleBuilder()
    }

    override fun getName(): String {
        return message("studio.project.name")
    }

    override fun getDescription(): String {
        return message("studio.project.description")
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return StudioIcons.STUDIO_ICON
    }

    companion object {
        private const val ID = "SFCC_STUDIO_MODULE"
        val instance: StudioModuleType
            get() = ModuleTypeManager.getInstance().findByID(ID) as StudioModuleType
    }
}
