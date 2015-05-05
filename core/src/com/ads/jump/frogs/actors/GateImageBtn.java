package com.ads.jump.frogs.actors;

import com.ads.jump.frogs.Assets;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by Administrator on 2014/7/5.
 */
public class GateImageBtn extends ImageButton {

    public GateImageBtn(Drawable imageUp, int id) {
        super(imageUp);
        float gateBtnSize = Assets.WIDTH / 6;
        float gateBtnSpace = Assets.WIDTH / 36;
        float y_off = Assets.HEIGHT / 4;
        float hspace = Assets.HEIGHT / 8;
        setBounds((id % 5 + 1) * gateBtnSpace + id % 5 * gateBtnSize, Assets.HEIGHT - y_off - id / 5 * hspace,gateBtnSize,gateBtnSize);
    }
}
