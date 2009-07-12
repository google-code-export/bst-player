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

package com.bramosystems.oss.player.core.event.client;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 *
 * @author Sikirulai Braheem <sbraheem at bramosystems.com>
 */
public class DebugEvent extends GwtEvent<DebugHandler> {
    public static final Type<DebugHandler> TYPE = new Type<DebugHandler>();
    private MessageType type;
    private String message;
  
    protected DebugEvent(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public static <S extends HasMediaStateHandlers> void fire(S source,
            MessageType type, String message) {
        source.fireEvent(new DebugEvent(type, message));
    }

    @Override
    public Type<DebugHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DebugHandler handler) {
        handler.onDebug(this);
    }
    
    public MessageType getMessageType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
    
    public enum MessageType {
        Error, Info
    }

}
