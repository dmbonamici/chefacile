//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package it.chefacile.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitchState;
import com.github.glomadrian.materialanimatedswitch.Utils;
import com.github.glomadrian.materialanimatedswitch.R.dimen;
import com.github.glomadrian.materialanimatedswitch.R.drawable;
import com.github.glomadrian.materialanimatedswitch.R.styleable;
import com.github.glomadrian.materialanimatedswitch.observer.BallFinishObservable;
import com.github.glomadrian.materialanimatedswitch.observer.BallMoveObservable;
import com.github.glomadrian.materialanimatedswitch.observer.BallFinishObservable.BallState;
import com.github.glomadrian.materialanimatedswitch.painter.BallPainter;
import com.github.glomadrian.materialanimatedswitch.painter.BallShadowPainter;
import com.github.glomadrian.materialanimatedswitch.painter.BasePainter;
import com.github.glomadrian.materialanimatedswitch.painter.IconPressPainter;
import com.github.glomadrian.materialanimatedswitch.painter.IconReleasePainter;
import java.util.Observable;
import java.util.Observer;

public class MaterialAnimatedSwitch extends View {
    private int margin;
    private BasePainter basePainter;
    private BallPainter ballPainter;
    private BallShadowPainter ballShadowPainter;
    private MaterialAnimatedSwitchState actualState;
    private IconPressPainter iconPressPainter;
    private IconReleasePainter iconReleasePainter;
    private int baseColorRelease = Color.parseColor("#3061BE");
    private int baseColorPress = Color.parseColor("#D7E7FF");
    private int ballColorRelease = Color.parseColor("#5992FB");
    private int ballColorPress = Color.parseColor("#FFFFFF");
    private int ballShadowColor = Color.parseColor("#99000000");
    private Bitmap releaseIcon;
    private Bitmap pressIcon;
    private BallFinishObservable ballFinishObservable;
    private BallMoveObservable ballMoveObservable;
    private boolean isClickable = true;
    private MaterialAnimatedSwitch.OnCheckedChangeListener onCheckedChangeListener;

    public MaterialAnimatedSwitch(Context context) {
        super(context);
        this.init();
    }

    public MaterialAnimatedSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public MaterialAnimatedSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    private void init() {
        this.margin = (int)this.getContext().getResources().getDimension(dimen.margin);
        this.initObservables();
        this.initPainters();
        this.actualState = MaterialAnimatedSwitchState.INIT;
        this.setState(this.actualState);
        this.setLayerType(1, (Paint)null);
    }

    private void initPainters() {
        this.basePainter = new BasePainter(this.baseColorRelease, this.baseColorPress, this.margin, this.ballMoveObservable);
        this.ballPainter = new BallPainter(this.ballColorRelease, this.ballColorPress, this.margin, this.ballFinishObservable, this.ballMoveObservable, this.getContext());
        this.ballShadowPainter = new BallShadowPainter(this.ballShadowColor, this.ballShadowColor, this.margin, this.ballShadowColor, this.ballFinishObservable, this.ballMoveObservable, this.getContext());
        this.iconPressPainter = new IconPressPainter(this.getContext(), this.pressIcon, this.ballFinishObservable, this.ballMoveObservable, this.margin);
        this.iconReleasePainter = new IconReleasePainter(this.getContext(), this.releaseIcon, this.ballFinishObservable, this.margin);
    }

    private void init(AttributeSet attrs) {
        TypedArray attributes = this.getContext().obtainStyledAttributes(attrs, styleable.materialAnimatedSwitch);
        this.initAttributes(attributes);
        this.init();
    }

    private void initAttributes(TypedArray attributes) {
        this.baseColorRelease = attributes.getColor(styleable.materialAnimatedSwitch_base_release_color, this.baseColorRelease);
        this.baseColorPress = attributes.getColor(styleable.materialAnimatedSwitch_base_press_color, this.baseColorPress);
        this.ballColorRelease = attributes.getColor(styleable.materialAnimatedSwitch_ball_release_color, this.ballColorRelease);
        this.ballColorPress = attributes.getColor(styleable.materialAnimatedSwitch_ball_press_color, this.ballColorPress);
        this.pressIcon = BitmapFactory.decodeResource(this.getResources(), attributes.getResourceId(styleable.materialAnimatedSwitch_icon_press, drawable.tack_save_button_32_blue));
        this.releaseIcon = BitmapFactory.decodeResource(this.getResources(), attributes.getResourceId(styleable.materialAnimatedSwitch_icon_release, drawable.tack_save_button_32_white));
    }

    private void initObservables() {
        this.ballFinishObservable = new BallFinishObservable();
        this.ballMoveObservable = new BallMoveObservable();
        this.ballFinishObservable.addObserver(new MaterialAnimatedSwitch.BallStateObserver());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = Utils.dpToPx(52.0F, this.getResources());
        int height = Utils.dpToPx(54.0F, this.getResources());
        this.setMeasuredDimension(width, height);
        this.basePainter.onSizeChanged(height, width);
        this.ballShadowPainter.onSizeChanged(height, width);
        this.ballPainter.onSizeChanged(height, width);
        this.iconPressPainter.onSizeChanged(height, width);
        this.iconReleasePainter.onSizeChanged(height, width);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.basePainter.draw(canvas);
        this.ballShadowPainter.draw(canvas);
        this.ballPainter.draw(canvas);
        this.iconPressPainter.draw(canvas);
        this.iconReleasePainter.draw(canvas);
        this.invalidate();
    }

    private void setState(MaterialAnimatedSwitchState materialAnimatedSwitchState) {
        this.basePainter.setState(materialAnimatedSwitchState);
        this.ballPainter.setState(materialAnimatedSwitchState);
        this.ballShadowPainter.setState(materialAnimatedSwitchState);
        this.iconPressPainter.setState(materialAnimatedSwitchState);
        this.iconReleasePainter.setState(materialAnimatedSwitchState);
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch(event.getAction()) {
            case 0:
                if(this.isClickable) {
                    this.doActionDown();
                }

                return true;
            default:
                return false;
        }
    }

    private void doActionDown() {
        if(!this.actualState.equals(MaterialAnimatedSwitchState.RELEASE) && !this.actualState.equals(MaterialAnimatedSwitchState.INIT) && this.actualState != null) {
            this.actualState = MaterialAnimatedSwitchState.RELEASE;
            this.setState(this.actualState);
        } else {
            this.actualState = MaterialAnimatedSwitchState.PRESS;
            this.setState(this.actualState);
        }

        this.playSoundEffect(0);
    }

    public boolean isChecked() {
        return this.actualState.equals(MaterialAnimatedSwitchState.PRESS);
    }

    public void toggle() {
        if(this.isClickable) {
            this.doActionDown();
        }

    }

    public void setOnCheckedChangeListener(MaterialAnimatedSwitch.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean var1);
    }

    private class BallStateObserver implements Observer {
        private BallStateObserver() {
        }

        public void update(Observable observable, Object data) {
            BallFinishObservable ballFinishObservable = (BallFinishObservable)observable;
            MaterialAnimatedSwitch.this.isClickable = !ballFinishObservable.getState().equals(BallState.MOVE);
            if(ballFinishObservable.getState().equals(BallState.PRESS)) {
                if(MaterialAnimatedSwitch.this.onCheckedChangeListener != null) {
                    MaterialAnimatedSwitch.this.onCheckedChangeListener.onCheckedChanged(true);
                }
            } else if(ballFinishObservable.getState().equals(BallState.RELEASE) && MaterialAnimatedSwitch.this.onCheckedChangeListener != null) {
                MaterialAnimatedSwitch.this.onCheckedChangeListener.onCheckedChanged(false);
            }

        }
    }
}
