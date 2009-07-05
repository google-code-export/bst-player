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
package com.bramosystems.oss.player.core.client.ui;

import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PluginVersion;
import com.bramosystems.oss.player.core.client.MediaInfo;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.bramosystems.oss.player.core.client.PlayException;
import com.bramosystems.oss.player.core.client.MediaStateListener;
import com.bramosystems.oss.player.core.client.MediaStateListenerAdapter;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.PlaylistSupport;
import com.bramosystems.oss.player.core.client.impl.FlashMediaPlayerImpl;
import com.bramosystems.oss.player.core.client.skin.FlatCustomControl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import java.util.ArrayList;

/**
 * Widget to embed Flash plugin for playback of flash-supported formats
 *
 * <h3>Usage Example</h3>
 *
 * <p>
 * <code><pre>
 * SimplePanel panel = new SimplePanel();   // create panel to hold the player
 * Widget player = null;
 * try {
 *      // create the player
 *      player = new FlashMediaPlayer("www.example.com/mediafile.flv", false, "200px", "250px");
 * } catch(LoadException e) {
 *      // catch loading exception and alert user
 *      Window.alert("An error occured while loading");
 * } catch(PluginVersionException e) {
 *      // catch plugin version exception and alert user to download plugin first.
 *      // An option is to use the utility method in PlayerUtil class.
 *      player = PlayerUtil.getMissingPluginNotice(Plugin.FlashMediaPlayer, "Missing Plugin",
 *              ".. some nice message telling the user to click and download plugin first ..",
 *              false);
 * } catch(PluginNotFoundException e) {
 *      // catch PluginNotFoundException and tell user to download plugin, possibly providing
 *      // a link to the plugin download page.
 *      player = new HTML(".. another kind of message telling the user to download plugin..");
 * }
 *
 * panel.setWidget(player); // add player to panel.
 * </pre></code>
 *
 * <h3>M3U Playlist Support</h3>
 * <p>
 * This player supports M3U formatted playlists.  However, each entry in the playlist MUST be
 * a flash-supported media file.
 * </p>
 *
 * @author Sikirulai Braheem
 * @since 1.0
 */
public class FlashMediaPlayer extends AbstractMediaPlayer implements PlaylistSupport {

    private static FlashMediaPlayerImpl impl = new FlashMediaPlayerImpl();
    private String playerId;
    private boolean isEmbedded;
    private Logger logger;
    private FlatCustomControl control;
    private MediaStateListener _onInitListListener;
    private ArrayList<String> _playlistCache;

    /**
     * Constructs <code>FlashMediaPlayer</code> with the specified {@code height} and
     * {@code width} to playback media located at {@code mediaURL}. Media playback
     * begins automatically if {@code autoplay} is {@code true}.
     *
     * <p> {@code height} and {@code width} are specified as CSS units. A value of {@code null}
     * for {@code height} or {@code width} puts the player in embedded mode.  When in embedded mode,
     * the player is made invisible on the page and media state events are propagated to registered
     * listeners only.  This is desired especially when used with custom sound controls.  For custom
     * video-playback control, specify valid CSS values for {@code height} and {@code width} but hide the
     * player controls with {@code setControllerVisible(false)}.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     * @param height the height of the player
     * @param width the width of the player.
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required Flash plugin version is not installed on the client.
     * @throws PluginNotFoundException if the Flash plugin is not installed on the client.
     */
    public FlashMediaPlayer(String mediaURL, boolean autoplay, String height, String width)
            throws PluginNotFoundException, PluginVersionException, LoadException {

        _playlistCache = new ArrayList<String>();

        isEmbedded = (height == null) || (width == null);
        if (isEmbedded) {
            height = "0px";
            width = "0px";
        }

        SWFWidget swf = new SWFWidget(GWT.getModuleBaseURL() + "bst-flash-player-1.0-SNAPSHOT.swf",
                "100%", "100%", PluginVersion.get(9, 0, 0));
        playerId = swf.getId();
        swf.addProperty("flashVars", "playerId=" + playerId);
        swf.addProperty("allowScriptAccess", "sameDomain");
        swf.addProperty("bgcolor", "#000000");

        impl.init(playerId, mediaURL, autoplay, new MediaStateListener() {

            public void onPlayFinished() {
                firePlayFinished();
            }

            public void onLoadingComplete() {
                fireLoadingComplete();
            }

            public void onError(String description) {
                fireError(description);
            }

            public void onDebug(String report) {
                fireDebug(report);
            }

            public void onLoadingProgress(double progress) {
                fireLoadingProgress(progress);
            }

            public void onPlayStarted() {
                firePlayStarted();
            }

            public void onPlayerReady() {
                firePlayerReady();
            }

            public void onMediaInfoAvailable(MediaInfo info) {
                fireMediaInfoAvailable(info);
            }

            public void onPlayStarted(int index) {
                firePlayStarted(index);
            }

            public void onPlayFinished(int index) {
                firePlayFinished(index);
            }

            public void onBuffering(boolean buffering) {
                fireBuffering(buffering);
            }
        });

        DockPanel hp = new DockPanel();

        if (!isEmbedded) {
            logger = new Logger();
            logger.setVisible(false);
            hp.add(logger, DockPanel.SOUTH);
            addMediaStateListener(new MediaStateListenerAdapter() {

                @Override
                public void onError(String description) {
                    log(description, false);
                }

                @Override
                public void onDebug(String message) {
                    log(message, false);
                }

                @Override
                public void onMediaInfoAvailable(MediaInfo info) {
                    log(info.asHTMLString(), true);
                }
            });
            control = new FlatCustomControl(this);
            hp.add(control, DockPanel.SOUTH);
        }

        hp.add(swf, DockPanel.CENTER);
        hp.setCellHeight(swf, height);
        hp.setCellWidth(swf, width);
        initWidget(hp);
        setWidth(width);
    }

    /**
     * Constructs <code>FlashMediaPlayer</code> to automatically playback media located at
     * {@code mediaURL}.
     *
     * <p> Note: This constructor hides the video display component, the player controls are
     * however visible.
     *
     * @param mediaURL the URL of the media to playback
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required Flash plugin version is not installed on the client.
     * @throws PluginNotFoundException if the Flash plugin is not installed on the client.
     *
     */
    public FlashMediaPlayer(String mediaURL) throws PluginNotFoundException,
            PluginVersionException, LoadException {
        this(mediaURL, true, "0px", "100%");
    }

    /**
     * Constructs <code>FlashMediaPlayer</code> to playback media located at {@code mediaURL}.
     * Media playback begins automatically if {@code autoplay} is {@code true}.
     *
     * <p> Note: This constructor hides the video display component, the player controls are
     * however visible.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required Flash plugin version is not installed on the client.
     * @throws PluginNotFoundException if the Flash plugin is not installed on the client.
     */
    public FlashMediaPlayer(String mediaURL, boolean autoplay) throws PluginNotFoundException,
            PluginVersionException, LoadException {
        this(mediaURL, autoplay, "0px", "100%");
    }

    public void close() {
        impl.closeMedia(playerId);
    }

    private void checkAvailable() {
        if (!impl.isPlayerAvailable(playerId)) {
            String message = "Player closed already, create another instance";
            fireDebug(message);
            throw new IllegalStateException(message);
        }
    }

    public long getMediaDuration() {
        checkAvailable();
        return (long) impl.getMediaDuration(playerId);
    }

    public double getPlayPosition() {
        checkAvailable();
        return impl.getPlayPosition(playerId);
    }

    public double getVolume() {
        checkAvailable();
        return impl.getVolume(playerId);
    }

    public void loadMedia(String mediaURL) throws LoadException {
        checkAvailable();
        impl.loadMedia(playerId, mediaURL);
    }

    public void pauseMedia() {
        checkAvailable();
        impl.pauseMedia(playerId);
    }

    public void playMedia() throws PlayException {
        checkAvailable();
        impl.playMedia(playerId);
    }

    public void setPlayPosition(double position) {
        checkAvailable();
        impl.setPlayPosition(playerId, position);
    }

    public void setVolume(double volume) {
        checkAvailable();
        impl.setVolume(playerId, volume);
    }

    public void stopMedia() {
        checkAvailable();
        impl.stopMedia(playerId);
    }

    @Override
    public void showLogger(boolean enable) {
        if (!isEmbedded) {
            logger.setVisible(enable);
        }
    }

    private void log(String message, boolean asHTML) {
        if (!isEmbedded && logger.isVisible()) {
            logger.log(message, asHTML);
        }
    }

    /**
     * Displays or hides the player controls.
     */
    @Override
    public void setControllerVisible(boolean show) {
        if (!isEmbedded) {
            control.setVisible(show);
        }
    }

    /**
     * Checks whether the player controls are visible.
     */
    @Override
    public boolean isControllerVisible() {
        return control.isVisible();
    }

    /**
     * Returns the number of times this player repeats playback before stopping.
     */
    @Override
    public int getLoopCount() {
        checkAvailable();
        return impl.getLoopCount(playerId);
    }

    /**
     * Sets the number of times the current media file should repeat playback before stopping.
     *
     * <p>As of version 1.0, if this player is not available on the panel, this method
     * call is added to the command-queue for later execution.
     */
    @Override
    public void setLoopCount(final int loop) {
        if (impl.isPlayerAvailable(playerId)) {
            impl.setLoopCount(playerId, loop);
        } else {
            addToPlayerReadyCommandQueue("loopcount", new Command() {

                public void execute() {
                    impl.setLoopCount(playerId, loop);
                }
            });
        }
    }

    public void addToPlaylist(final String mediaURL) {
        if (impl.isPlayerAvailable(playerId)) {
            impl.addToPlaylist(playerId, mediaURL);
        } else {
            if (!containsMediaStateListener(_onInitListListener)) {
                _onInitListListener = new MediaStateListenerAdapter() {

                    @Override
                    public void onPlayerReady() {
                        for (String url : _playlistCache) {
                            impl.addToPlaylist(playerId, url);
                        }
                        removeMediaStateListener(_onInitListListener);
                    }
                };
                addMediaStateListener(_onInitListListener);
            }
            _playlistCache.add(mediaURL);
        }
    }

    public boolean isShuffleEnabled() {
        checkAvailable();
        return impl.isShuffleEnabled(playerId);
    }

    public void removeFromPlaylist(int index) {
        checkAvailable();
        impl.removeFromPlaylist(playerId, index);
    }

    /**
     * Enables or disables players' shuffle mode.
     *
     * <p>As of version 1.0, if this player is not available on the panel, this method
     * call is added to the command-queue for later execution.
     */
    public void setShuffleEnabled(final boolean enable) {
        if (impl.isPlayerAvailable(playerId)) {
            impl.setShuffleEnabled(playerId, enable);
        } else {
            addToPlayerReadyCommandQueue("shuffle", new Command() {

                public void execute() {
                    impl.setShuffleEnabled(playerId, enable);
                }
            });
        }
    }

    public void clearPlaylist() {
        checkAvailable();
        impl.clearPlaylist(playerId);
    }

    public int getPlaylistSize() {
        checkAvailable();
        return impl.getPlaylistCount(playerId);
    }

    public void play(int index) throws IndexOutOfBoundsException {
        checkAvailable();
        if(!impl.playMedia(playerId, index)) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void playNext() throws PlayException {
        checkAvailable();
        if(!impl.playNext(playerId)) {
            throw new PlayException("No more entries in playlist");
        }
    }

    public void playPrevious() throws PlayException {
        checkAvailable();
        if(!impl.playPrevious(playerId)) {
            throw new PlayException("Beginning of playlist reached");
        }
    }
}