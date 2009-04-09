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
package com.bramosystems.gwt.player.core.client.ui;

import com.bramosystems.gwt.player.core.client.PluginVersion;
import com.bramosystems.gwt.player.core.client.PlayerUtil;
import com.bramosystems.gwt.player.core.client.PluginVersionException;
import com.bramosystems.gwt.player.core.client.PluginNotFoundException;
import com.bramosystems.gwt.player.core.client.Plugin;
import com.bramosystems.gwt.player.core.client.impl.SWFWidgetImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import java.util.HashMap;

/**
 * Widget to embed a generic Shockwave Flash application.
 *
 * <h3>Usage Example</h3>
 *
 * <p>
 * <code><pre>
 * SimplePanel panel = new SimplePanel();   // create panel to hold the widget
 * Widget swf = null;
 * try {
 *      // create the player
 *      swf = new SWFWidget("www.example.com/mediafile.swf", "250px", "200px",
 *                          PluginVersion.get(8, 0, 0));
 *      swf.addProperty("allowScriptAccess", "true");  // Note: add SWF property before adding object to panel.
 *      swf.addProperty("flashVars", "param1=value1&param2=value2");
 * } catch(PluginVersionException e) {
 *      // catch plugin version exception and alert user to download plugin first.
 *      // An option is to use the utility method - {@link #getMissingPluginNotice(String, String, boolean)
 *      swf = SWFWidget.getMissingPluginNotice("Missing Plugin",
 *              ".. some nice message telling the user to click and download plugin first ..",
 *              false);
 * } catch(PluginNotFoundException e) {
 *      // catch PluginNotFoundException and tell user to download plugin, possibly providing
 *      // a link to the plugin download page.
 *      swf = new HTML(".. another kind of message telling the user to download plugin..");
 * }
 *
 * panel.setWidget(swf); // add object to panel.
 * </pre></code>
 *
 * @since 0.6
 * @author Sikirulai Braheem
 */
public class SWFWidget extends Composite {

    private static SWFWidgetImpl impl;
    private String playerId,  swfURL;
    private SimplePanel playerDiv;
    private HashMap<String, String> params;

    /**
     * Constructs <code>SWFWidget</code> with the specified {@code height} and
     * {@code width} to embed Flash application located at {@code sourceURL} if the client
     * has a Flash plugin of version {@code minFlashVersion} or later installed.
     *
     * <p> {@code height} and {@code width} are specified as CSS units.
     *
     * @param sourceURL the URL of the media to playback
     * @param height the height of the player
     * @param width the width of the player.
     * @param minFlashVersion minimum version of the required Flash plugin
     *
     * @throws com.bramosystems.gwt.player.client.LoadException if an error occurs while loading the media.
     * @throws com.bramosystems.gwt.player.client.PluginVersionException if the required
     * Flash plugin version is not installed on the client.
     * @throws com.bramosystems.gwt.player.client.PluginNotFoundException if the Flash plugin is not
     * installed on the client.
     * @throws NullPointerException if {@code height} or {@code width} is null.
     *
     * @see PluginVersion
     */
    public SWFWidget(String sourceURL, String width, String height, PluginVersion minFlashVersion)
            throws PluginNotFoundException, PluginVersionException {

        PluginVersion v = PlayerUtil.getFlashPlayerVersion();
        if (v.compareTo(minFlashVersion) < 0) {
            throw new PluginVersionException(minFlashVersion.toString(), v.toString());
        }

        if (impl == null) {
            impl = GWT.create(SWFWidgetImpl.class);
        }

        if (height == null) {
            throw new NullPointerException("height cannot be null");
        }
        if (width == null) {
            throw new NullPointerException("width cannot be null");
        }

        this.swfURL = sourceURL;
        playerId = DOM.createUniqueId().replace("-", "");
        params = new HashMap<String, String>();

        playerDiv = new SimplePanel();
        playerDiv.getElement().setId(playerId + "_div");

        initWidget(playerDiv);
        setSize(width, height);
    }

    /**
     * Adds the property {@code name} with value {@code value} to the properties list of the
     * Flash object.
     *
     * <p>Adding a property more than once removes previous values and only the last value is
     * used. Also adding a null or empty value for a named property removes the property from the
     * list if it has been added before.
     *
     * <p>The following properties are included by default and should not be used with this method:
     * <ul>
     * <li><b>id, name</b> - autogenerated</li>
     * <li><b>movie, src</b> - specified with the contructor method</li>
     * <li><b>height, width</b> - specified with the contructor method</li>
     * <li><b>type</b> - included by default where necessary</li>
     * <li><b>classid</b> - included by default where necessary</li>
     * </ul>
     *
     * <p><em><b>Note:</b> For all named properties to take effect, this method should be called before
     * adding this widget to a panel.</em>
     *
     * @param name property name
     * @param value property value
     */
    public void addProperty(String name, String value) {
        if (!isEmpty(name)) {
            if (isEmpty(value) && params.containsKey(name)) {
                params.remove(name);
            } else if (!isEmpty(value)) {
                params.put(name, value);
            }
        }
    }

    @Override
    protected void onLoad() {
        Timer t = new Timer() {

            @Override
            public void run() {
                impl.injectScript(playerId, swfURL, getOffsetWidth(), getOffsetHeight(), params);
            }
        };
        t.schedule(500);   // IE & Opera workarround...
    }

    /**
     * Subclasses that override this method should call <code>super.onUnload()</code>
     * to ensure the SWF object is properly removed from the browser's DOM.
     */
    @Override
    protected void onUnload() {
        playerDiv.getElement().setInnerText("");
    }

    private boolean isEmpty(String value) {
        return (value == null) || (value.length() == 0);
    }

    /**
     * Returns the ID of this object.  The ID is the value assigned to the
     * {@code id} and {@code name} attributes of the generated object and
     * embed tags.
     *
     * @return the ID of the object
     */
    public String getId() {
        return playerId;
    }

    /**
     * Returns a widget that may be used to notify the user when the required Flash
     * plugin is not available.  The widget provides a link to the Flash plugin
     * download page.
     *
     * @param title the title of the message
     * @param message descriptive message to notify user about the missing plugin
     * @param asHTML {@code true} if {@code message} should be interpreted as HTML,
     *          {@code false} otherwise.
     *
     * @return missing plugin widget.
     *
     * @see PlayerUtil#getMissingPluginNotice(Plugin, String, String, boolean)
     */
    public static Widget getMissingPluginNotice(String title, String message, boolean asHTML) {
        return PlayerUtil.getMissingPluginNotice(Plugin.FlashMP3Player, title, message, asHTML);
    }

    /**
     * Convinience method to get a widget that may be used to notify the user when
     * the required Flash plugin is not available.
     *
     * <p>This is same as calling {@code getMissingPluginNotice("Missing Plugin",
     *      "Adobe Flash Player [version] or later is required. Click here to get Flash", false)}
     *
     * @param version the required Flash version
     *
     * @return missing plugin widget.
     *
     * @see #getMissingPluginNotice(String, String, boolean)
     */
    public static Widget getMissingPluginNotice(PluginVersion version) {
        return PlayerUtil.getMissingPluginNotice(Plugin.FlashMP3Player, version.toString());
    }
}