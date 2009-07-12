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
package com.bramosystems.oss.player.capsule.client;

import com.bramosystems.oss.player.core.client.*;
import com.bramosystems.oss.player.core.client.skin.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import java.util.ArrayList;

/**
 * Sample custom sound player.
 *
 * <h3>Usage Example</h3>
 *
 * <p>
 * <code><pre>
 * SimplePanel panel = new SimplePanel();   // create panel to hold the player
 * Widget player = null;
 * try {
 *      // create the player
 *      player = new Capsule("www.example.com/mediafile.wma");
 * } catch(LoadException e) {
 *      // catch loading exception and alert user
 *      Window.alert("An error occured while loading");
 * } catch(PluginVersionException e) {
 *      // catch plugin version exception and alert user, possibly providing a link
 *      // to the plugin download page.
 *      player = new HTML(".. some nice message telling the user to download plugin first ..");
 * } catch(PluginNotFoundException e) {
 *      // catch PluginNotFoundException and tell user to download plugin, possibly providing
 *      // a link to the plugin download page.
 *      player = new HTML(".. another kind of message telling the user to download plugin..");
 * }
 *
 * panel.setWidget(player); // add player to panel.
 * </pre></code>
 *
 * @author Sikirulai Braheem
 */
public class Capsule extends CustomAudioPlayer {

    private CapsuleImages imgPack = GWT.create(CapsuleImages.class);
    private ProgressBar progress;
    private PushButton play,  stop;
    private Timer playTimer,  infoTimer;
    private PlayState playState;
    private VolumeControl vc;
    private MediaInfo mInfo;
    private ArrayList<MediaInfo.MediaInfoKey> mItems;
    private int infoIndex;

    /**
     * Constructs <code>Capsule</code> player to automatically playback the
     * media located at {@code mediaURL}.
     *
     * @param mediaURL the URL of the media to playback
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required plugin version is not installed on the client.
     * @throws PluginNotFoundException if the plugin is not installed on the client.
     */
    public Capsule(String mediaURL) throws PluginNotFoundException,
            PluginVersionException, LoadException {
        this(mediaURL, true);
    }

    /**
     * Constructs <code>Capsule</code> player to automatically playback the
     * media located at {@code mediaURL}, if {@code autoplay} is {@code true}.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required plugin version is not installed on the client.
     * @throws PluginNotFoundException if the plugin is not installed on the client.
     */
    public Capsule(String mediaURL, boolean autoplay) throws PluginNotFoundException,
            PluginVersionException, LoadException {
        this(Plugin.Auto, mediaURL, autoplay);
    }

    /**
     * Constructs <code>Capsule</code> player to automatically playback the
     * media located at {@code mediaURL}, if {@code autoplay} is {@code true} using
     * the specified {@code plugin}.
     *
     * @param plugin plugin to use for playback.
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to start playing automatically, {@code false} otherwise
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required plugin version is not installed on the client.
     * @throws PluginNotFoundException if the plugin is not installed on the client.
     *
     * @see Plugin
     */
    public Capsule(Plugin plugin, String mediaURL, boolean autoplay) throws PluginNotFoundException,
            PluginVersionException, LoadException {
        super(plugin, mediaURL, autoplay, "64px", "100%");

        playState = PlayState.Stop;
        mItems = new ArrayList<MediaInfo.MediaInfoKey>();

        progress = new ProgressBar();
        progress.setWidth("95%");

        playTimer = new Timer() {

            @Override
            public void run() {
                progress.setTime(getPlayPosition());
            }
        };
        infoTimer = new Timer() {

            @Override
            public void run() {
                if (mItems.size() > 0) {
                    MediaInfo.MediaInfoKey item = mItems.get(infoIndex);
                    progress.setInfo(item.toString() + ": " + mInfo.getItem(item));
                    infoIndex++;
                    infoIndex %= mItems.size();
                } else {
                    cancel();
                }
            }

            @Override
            public void cancel() {
                super.cancel();
                progress.setInfo("");
            }
        };

        play = new PushButton(imgPack.play().createImage(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                switch (playState) {
                    case Stop:
                    case Pause:
                        try { // play media...
                            playMedia();
                        } catch (PlayException ex) {
                            fireError(ex.getMessage());
                        }
                        break;
                    case Playing:
                        pauseMedia();
                        toPlayState(PlayState.Pause);
                }
            }
        });

        play.getUpDisabledFace().setImage(imgPack.playDisabled().createImage());
        play.setEnabled(false);

        stop = new PushButton(imgPack.stop().createImage(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                stopMedia();
                toPlayState(PlayState.Stop);
            }
        });
        stop.getUpDisabledFace().setImage(imgPack.stopDisabled().createImage());
        stop.getUpHoveringFace().setImage(imgPack.stopHover().createImage());
        stop.setEnabled(false);

        vc = new VolumeControl(imgPack.spk().createImage(), 5);
        vc.addVolumeChangeListener(new VolumeChangeListener() {

            public void onVolumeChanged(double newValue) {
                setVolume(newValue);
            }
        });
        vc.setPopupStyleName("player-Capsule-volumeControl");

        addMediaStateListener(new MediaStateListenerAdapter() {

            @Override
            public void onError(String description) {
                Window.alert(description);
                onDebug(description);
            }

            @Override
            public void onLoadingComplete() {
                progress.setLoadingProgress(1);
                progress.setDuration(getMediaDuration());
                progress.setTime(0);
                vc.setVolume(getVolume());
            }

            @Override
            public void onPlayFinished(int index) {
                toPlayState(PlayState.Stop);
            }

            @Override
            public void onDebug(String report) {
                logger.log(report, false);
            }

            @Override
            public void onLoadingProgress(double progrezz) {
                progress.setLoadingProgress(progrezz);
            }

            @Override
            public void onPlayStarted(int index) {
                toPlayState(PlayState.Playing);
            }

            @Override
            public void onPlayerReady() {
                play.setEnabled(true);
                vc.setVolume(getVolume());
            }

            @Override
            public void onMediaInfoAvailable(MediaInfo info) {
                mInfo = info;
                mItems = mInfo.getAvailableItems();
                mItems.remove(MediaInfo.MediaInfoKey.Comment);
                mItems.remove(MediaInfo.MediaInfoKey.Duration);
                mItems.remove(MediaInfo.MediaInfoKey.HardwareSoftwareRequirements);
                logger.log(info.asHTMLString(), true);
            }
        });

        // build the UI...
        DockPanel main = new DockPanel();
        main.setStyleName("player-Capsule");
        main.setSpacing(0);
        main.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
        main.setSize("100%", "64px");

        main.add(imgPack.lEdge().createImage(), DockPanel.WEST);
        main.add(play, DockPanel.WEST);
        main.add(stop, DockPanel.WEST);
        main.add(imgPack.rEdge().createImage(), DockPanel.EAST);
        main.add(vc, DockPanel.EAST);
        main.add(progress, DockPanel.CENTER);
        main.setCellWidth(progress, "100%");
        main.setCellHorizontalAlignment(progress, DockPanel.ALIGN_CENTER);
        setPlayerControlWidget(main);
        setWidth("100%");
    }

    /**
     * Overridden to release resources.
     */
    @Override
    protected void onUnload() {
        playTimer.cancel();
        infoTimer.cancel();
    }

    private void toPlayState(PlayState state) {
        switch (state) {
            case Playing:
                play.setEnabled(true);
                stop.setEnabled(true);
                vc.setVolume(getVolume());
                playTimer.scheduleRepeating(1000);
                infoTimer.scheduleRepeating(3000);

                play.getUpFace().setImage(imgPack.pause().createImage());
                play.getUpHoveringFace().setImage(imgPack.pauseHover().createImage());
                break;
            case Stop:
                progress.setTime(0);
                progress.setFinishedState();
                stop.setEnabled(false);
                playTimer.cancel();
                infoTimer.cancel();
            case Pause:
                play.getUpFace().setImage(imgPack.play().createImage());
                play.getUpHoveringFace().setImage(imgPack.playHover().createImage());
                break;
        }
        playState = state;
    }

    private class ProgressBar extends Composite {

        private MediaSeekBar seekBar;
        private Label timeLabel,  infoLabel;
        private long duration;
        private String durationString;

        public ProgressBar() {
            timeLabel = new Label("--:-- / --:--");
            timeLabel.setStyleName("player-Capsule-info");
            timeLabel.setWordWrap(false);
            timeLabel.setHorizontalAlignment(Label.ALIGN_RIGHT);

            infoLabel = new Label();
            infoLabel.setStyleName("player-Capsule-info");
            infoLabel.setWordWrap(false);

            DockPanel dp = new DockPanel();
            dp.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
            dp.setWidth("100%");

            dp.add(timeLabel, DockPanel.EAST);
            dp.add(infoLabel, DockPanel.CENTER);

            seekBar = new CSSSeekBar(5);
            seekBar.setStylePrimaryName("player-Capsule-seekbar");
            seekBar.setWidth("100%");
            seekBar.addSeekChangeListener(new SeekChangeListener() {

                public void onSeekChanged(double newValue) {
                    double newTime = getMediaDuration() * newValue;
                    setPlayPosition(newTime);
                }
            });

            VerticalPanel main = new VerticalPanel();
            main.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
            main.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
            main.setSpacing(2);
            main.setWidth("80%");
            main.add(dp);
            main.add(seekBar);
            initWidget(main);
        }

        public void setTime(double timeInMS) {
            timeLabel.setText(PlayerUtil.formatMediaTime((long) timeInMS) + " / " + durationString);
            seekBar.setPlayingProgress(timeInMS / (double) duration);
        }

        public void setLoadingProgress(double progress) {
            seekBar.setLoadingProgress(progress);
        }

        public void setDuration(long duration) {
            this.duration = duration;
            durationString = PlayerUtil.formatMediaTime(duration);
        }

        public void setInfo(String info) {
            infoLabel.setText(info);
        }

        public void setFinishedState() {
            setTime(0);
            seekBar.setPlayingProgress(0);
        }
    }

    private enum PlayState {

        Playing, Pause, Stop;
    }
}