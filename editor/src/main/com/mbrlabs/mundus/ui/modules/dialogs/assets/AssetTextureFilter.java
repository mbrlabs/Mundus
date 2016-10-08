package com.mbrlabs.mundus.ui.modules.dialogs.assets;

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.TextureAsset;

/**
 * @author Marcus Brummer
 * @version 07-10-2016
 */
public class AssetTextureFilter implements AssetSelectionDialog.AssetFilter {

    @Override
    public boolean ignore(Asset asset) {
        return !(asset instanceof TextureAsset);
    }

}
