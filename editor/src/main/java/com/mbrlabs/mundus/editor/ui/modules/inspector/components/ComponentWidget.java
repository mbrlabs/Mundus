/*
 * Copyright (c) 2016. See AUTHORS file.
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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components;

import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;

/**
 * @author Marcus Brummer
 * @version 22-01-2016
 */
public abstract class ComponentWidget<T extends Component> extends BaseInspectorWidget {

    public T component;

    public ComponentWidget(String title, T component) {
        super(title);
        setDeletable(true);
        this.component = component;
    }

    @Override
    public void onDelete() {
        component.remove();
        remove();
    }

}
