package com.mbrlabs.mundus.ui.components.dialogs;

import com.kotcrab.vis.ui.widget.VisDialog;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class BaseDialog extends VisDialog {

    public BaseDialog(String title) {
        super(title);
        addCloseButton();
    }

//    @Override
//    public void addCloseButton() {
//        VisImageButton closeButton = new VisImageButton("close-window");
//        this.getTitleTable().add(closeButton).padBottom(2);
//        closeButton.addListener(new ChangeListener() {
//            public void changed(ChangeEvent event, Actor actor) {
//                O2DDialog.this.close();
//            }
//        });
//
//        if (this.getTitleTable().getChildren().size == 2) {
//            this.getTitleTable().getCell(this.getTitleLabel()).padLeft(closeButton.getWidth() * 2.0F);
//        }
//    }

}
