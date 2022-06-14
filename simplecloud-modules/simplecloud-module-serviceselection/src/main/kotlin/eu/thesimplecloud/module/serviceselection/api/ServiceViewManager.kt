/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.serviceselection.api

import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

open class ServiceViewManager<T : AbstractServiceViewer>(
    private val plugin: JavaPlugin,
    private val updateDelay: Long = 20
) {

    private val groupNameToGroupView = HashMap<String, ServiceViewGroupManager<T>>()

    init {
        startUpdateScheduler()
    }

    private fun startUpdateScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            performUpdate()
        }, 20, this.updateDelay)
    }

    fun addServiceViewGroupManager(serviceViewGroupManager: ServiceViewGroupManager<T>) {
        this.groupNameToGroupView[serviceViewGroupManager.group.getName()] = serviceViewGroupManager
    }

    fun getGroupView(group: ICloudServiceGroup): ServiceViewGroupManager<T> {
        return this.groupNameToGroupView.getOrPut(group.getName()) { ServiceViewGroupManager(group) }
    }

    fun isGroupViewRegistered(group: ICloudServiceGroup): Boolean {
        return this.groupNameToGroupView.containsKey(group.getName())
    }

    fun getAllGroupViewManagers(): Collection<ServiceViewGroupManager<T>> {
        return this.groupNameToGroupView.values
    }

    open fun performUpdate() {
        val groups = this.groupNameToGroupView.values
        groups.forEach { it.sortWaitingServicesToViewers() }
        groups.forEach { it.updateAllViewers() }
    }


}
