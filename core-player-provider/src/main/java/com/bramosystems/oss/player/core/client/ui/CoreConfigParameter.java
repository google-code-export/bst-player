/*
 * Copyright 2012 Sikiru Braheem <sbraheem at bramosystems . com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bramosystems.oss.player.core.client.ui;

import com.bramosystems.oss.player.core.client.ConfigParameter;
import com.bramosystems.oss.player.core.client.ui.QuickTimePlayer.Scale;
import com.bramosystems.oss.player.core.client.ui.WinMediaPlayer.UIMode;

/**
 * Configuration parameters for the core player widgets.
 *
 * @author Sikiru Braheem <sbraheem at bramosystems . com>
 * @since 2.0
 */
public enum CoreConfigParameter implements ConfigParameter {

    /**
     * Parameter for WinMediaPlayers' UI Mode property.
     *
     * <p>The mode indicates which controls are shown on the user interface.</p>
     * <p>This parameter requires a {@linkplain UIMode} value type
     */
    WMPUIMode(UIMode.class),
    
    /**
     * Parameter for QuickTimePlayers' Scale property.
     *
     * <p>This parameter is used to scale the dimensions of a QuickTime movie. It requires either a
     * {@linkplain Scale} value type or a double value</p>
     *
     * <p>A double value scales the movie by a factor of the value. For example, to play a movie at 
     * half its normal size, use QTScale with a value of 0.5</p>
     *
     * @see Scale
     */
    QTScale(Scale.class, Double.class);
    
    private Class[] valueType;

    private CoreConfigParameter(Class... valueType) {
        this.valueType = valueType;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Class[] getValueType() {
        return valueType;
    }
}
