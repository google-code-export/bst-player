/*
 * Copyright 2009 Sikirulai Braheem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bramosystems.oss.player.core.client.impl;

import com.bramosystems.oss.player.core.client.PluginVersion;
import java.io.Serializable;

/**
 * Wraps basic information about a browser plug-in component.
 *
 * @author Sikirulai Braheem
 * @since 1.2.1
 */
public class PluginInfo implements Serializable {

    /**
     * An enum of javascript API wrappers available for the plugin
     *
     * @since 1.2.1
     */
    public static enum PlayerPluginWrapperType {
        /**
         * The player plugin is exposed to javascript using its native API
         */
        Native,

        /**
         * Used only for Windows Media Player plugin.  The player is exposed by the
         * Windows Media Player plugin for Firefox
         */
        WMPForFirefox,

        /**
         * The player plugin wrapped and exposed to javascript by the Totem plugin
         */
        Totem
    }

    private PluginVersion version;
    private PlayerPluginWrapperType wrapperType;

    /**
     * Creates a <code>PluginVersion</code>
     */
    public PluginInfo() {
        version = new PluginVersion();
        wrapperType = PlayerPluginWrapperType.Native;
    }

    /**
     * Creates a <code>PluginInfo</code> with the specified version and wrapperType
     *
     * @param version the plugin version
     * @param wrapperType the wrapper type in use by the plugin
     */
    public PluginInfo(PluginVersion version, PlayerPluginWrapperType wrapperType) {
        this.version = version;
        this.wrapperType = wrapperType;
    }

    public PluginVersion getVersion() {
        return version;
    }

    public void setVersion(PluginVersion version) {
        this.version = version;
    }

    public PlayerPluginWrapperType getWrapperType() {
        return wrapperType;
    }

    public void setWrapperType(PlayerPluginWrapperType wrapperType) {
        this.wrapperType = wrapperType;
    }

    @Override
    public String toString() {
        return "PluginInfo{" + "version=" + version + ",  wrapperType=" + wrapperType + "} ";
    }


}
