package com.stario.launcher.ui.liquidglass;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * iOS 26 Liquid Glass helper for Stario.
 * Membungkus com.qmdeve.liquidglass dengan fallback untuk API <33.
 * 
 * Elemen iOS 26 yang diimitasi:
 * - Material: translucent blur + refraction + dispersion (real-time highlight saat gerak)
 * - Glass.regular / Glass.clear / Glass.identity -> via height, blurRadius, tone
 * - Morphing & spring animation (jelly) -> via elastic & highlight params
 * - SF Symbols 7 analog -> vector drawable 9 weights, 3 scales
 * 
 * Library utama: com.qmdeve.liquidglass:core:1.0.5 (View-based, cocok untuk Stario View XML)
 * Alternatif jika butuh Compose: io.github.kyant0:backdrop (Kyant0/AndroidLiquidGlass)
 */
public final class LiquidGlassHelper {
    private LiquidGlassHelper() {}

    public static final int IOS26_CORNER_DOCK = 36; // dp, iOS 26 dock radius
    public static final int IOS26_CORNER_CARD = 24;
    public static final int IOS26_CORNER_BUTTON = 18;
    public static final int IOS26_CORNER_SHEET = 32;
    public static final float IOS26_BLUR_REGULAR = 24f;
    public static final float IOS26_BLUR_CLEAR = 12f;
    public static final float IOS26_REFRACTION_HEIGHT = 10f;
    public static final float IOS26_DISPERSION = 0.4f;

    /**
     * Wrap view jadi LiquidGlassView jika API >=33, fallback semi-transparent + blur jika <33
     */
    public static View wrap(@NonNull Context ctx, @NonNull View content, GlassVariant variant) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                // Refleksi agar tidak crash kalau library belum ada saat compile
                Class<?> cls = Class.forName("com.qmdeve.liquidglass.LiquidGlassView");
                View glass = (View) cls.getConstructor(Context.class).newInstance(ctx);
                // Set params via refleksi
                cls.getMethod("setCornerRadius", float.class).invoke(glass, dp(ctx, variant.cornerDp));
                cls.getMethod("setBlurRadius", float.class).invoke(glass, variant.blur);
                cls.getMethod("setRefractionHeight", float.class).invoke(glass, variant.refraction);
                cls.getMethod("setDispersion", float.class).invoke(glass, variant.dispersion);
                cls.getMethod("setTone", int.class).invoke(glass, variant.tone);
                // Add content inside
                if (glass instanceof ViewGroup) {
                    ((ViewGroup) glass).addView(content);
                }
                return glass;
            } catch (Exception e) {
                // fallback
                return fallback(ctx, content, variant);
            }
        } else {
            return fallback(ctx, content, variant);
        }
    }

    private static View fallback(Context ctx, View content, GlassVariant variant) {
        // Android 10-12: translucent material + outline, tanpa refraction
        android.widget.FrameLayout wrapper = new android.widget.FrameLayout(ctx);
        wrapper.setBackgroundResource(variant.fallbackDrawable);
        wrapper.setClipToOutline(true);
        wrapper.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                outline.setRoundRect(0,0,view.getWidth(),view.getHeight(), dp(ctx, variant.cornerDp));
            }
        });
        wrapper.addView(content);
        content.setBackgroundColor(Color.argb(30, 255,255,255));
        return wrapper;
    }

    private static float dp(Context c, float v){ return v * c.getResources().getDisplayMetrics().density; }

    public enum GlassVariant {
        REGULAR(IOS26_CORNER_CARD, IOS26_BLUR_REGULAR, IOS26_REFRACTION_HEIGHT, IOS26_DISPERSION, Color.argb(40,255,255,255), android.R.drawable.dialog_holo_light_frame),
        CLEAR(IOS26_CORNER_BUTTON, IOS26_BLUR_CLEAR, 6f, 0.2f, Color.argb(20,255,255,255), android.R.drawable.dialog_holo_light_frame),
        PROMINENT(IOS26_CORNER_DOCK, 30f, 12f, 0.5f, Color.argb(60,255,255,255), android.R.drawable.dialog_holo_light_frame),
        SHEET(IOS26_CORNER_SHEET, 28f, 10f, 0.45f, Color.argb(35,255,255,255), android.R.drawable.dialog_holo_light_frame);

        public final float cornerDp, blur, refraction, dispersion;
        public final int tone, fallbackDrawable;
        GlassVariant(float c,float b,float r,float d,int t,int f){ cornerDp=c; blur=b; refraction=r; dispersion=d; tone=t; fallbackDrawable=f; }
    }

    /** Apply iOS 26 buttonStyle .glass / .glassProminent ke view */
    public static void styleGlassButton(View button, boolean prominent) {
        GlassVariant v = prominent ? GlassVariant.PROMINENT : GlassVariant.REGULAR;
        button.setClipToOutline(true);
        button.setOutlineProvider(new android.view.ViewOutlineProvider(){
            @Override public void getOutline(View view, android.graphics.Outline outline){
                outline.setRoundRect(0,0,view.getWidth(),view.getHeight(), v.cornerDp * view.getResources().getDisplayMetrics().density);
            }
        });
        button.setBackgroundColor(v.tone);
        // spring animation on press (imitasi jelly)
        button.setOnTouchListener((view, e)->{
            if(e.getAction()==android.view.MotionEvent.ACTION_DOWN){
                view.animate().scaleX(0.96f).scaleY(0.96f).setDuration(120)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(2f)).start();
            } else if(e.getAction()==android.view.MotionEvent.ACTION_UP || e.getAction()==android.view.MotionEvent.ACTION_CANCEL){
                view.animate().scaleX(1f).scaleY(1f).setDuration(300)
                    .setInterpolator(new android.view.animation.BounceInterpolator()).start();
            }
            return false;
        });
    }
}
