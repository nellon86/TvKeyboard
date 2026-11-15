package com.liskovsoft.leankeyboard.addons.keyboards.intkeyboards;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import com.liskovsoft.leankeyboard.addons.keyboards.KeyboardBuilder;
import com.liskovsoft.leankeyboard.addons.keyboards.KeyboardFactory;
import com.liskovsoft.leankeyboard.addons.keyboards.KeyboardInfo;
import com.liskovsoft.leankeyboard.ime.LeanbackKeyboardView;
import com.liskovsoft.leankeyboard.utils.LeanKeyPreferences;
import com.liskovsoft.leankeyboard.utils.TextDrawable;
import com.liskovsoft.leankeykeyboard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResKeyboardFactory implements KeyboardFactory {
    private static final String TAG = ResKeyboardFactory.class.getSimpleName();
    private static final String MODE_LABEL_SYMBOLS = "?123";
    private static final String MODE_LABEL_LETTERS = "ABC|ABC"; // same label in both cases
    private static final float SPACE_TEXT_SIZE_FACTOR = 0.4f;
    private final Context mContext;
    private Map<String, Drawable> mCachedSpace;

    public ResKeyboardFactory(Context ctx) {
        mContext = ctx;
        mCachedSpace = new HashMap<>();
    }

    @Override
    public List<? extends KeyboardBuilder> getAllAvailableKeyboards(Context context) {
        List<KeyboardBuilder> result = new ArrayList<>();
        List<KeyboardInfo> infos = ResKeyboardInfo.getAllKeyboardInfos(context);

        for (final KeyboardInfo info : infos) {
            if (info.isEnabled()) {
                result.add(createKeyboard(info));
            }
        }

        // at least one kbd should be enabled
        if (result.isEmpty()) {
            KeyboardInfo defaultKbd = findDefaultKeyboard(infos);
            result.add(createKeyboard(defaultKbd));
            defaultKbd.setEnabled(true);
            //ResKeyboardInfo.updateAllKeyboardInfos(mContext, infos);
        }

        return result;
    }

    private KeyboardInfo findDefaultKeyboard(List<KeyboardInfo> infos) {
        KeyboardInfo defaultKeyboard = infos.get(0);

        if (LeanKeyPreferences.instance(mContext).getAutodetectLayout()) {
            Locale defaultLocale = Locale.getDefault();
            String lang = defaultLocale.getLanguage();

            for (final KeyboardInfo info : infos) {
                if (info.getLangCode().startsWith(lang)) {
                    defaultKeyboard = info;
                    break;
                }
            }
        }

        return defaultKeyboard;
    }

    /**
     * NOTE: create keyboard from xml data
     */
    private KeyboardBuilder createKeyboard(final KeyboardInfo info) {
        return new KeyboardBuilder() {
            private final String langCode = info.getLangCode();

            @Override
            public Keyboard createAbcKeyboard() {
                String prefix = info.isAzerty() ? "azerty_" : "qwerty_";
                int kbResId = mContext.getResources().getIdentifier(prefix + langCode, "xml", mContext.getPackageName());
                Keyboard keyboard = new Keyboard(mContext, kbResId);
                Log.d(TAG, "Creating keyboard... " + info.getLangName());
                return localizeKeys(keyboard, info, MODE_LABEL_SYMBOLS);
            }

            @Override
            public Keyboard createSymKeyboard() {
                Keyboard keyboard = new Keyboard(mContext, R.xml.sym_en_us);
                return localizeKeys(keyboard, info, MODE_LABEL_LETTERS);
            }

            @Override
            public Keyboard createNumKeyboard() {
                return new Keyboard(mContext, R.xml.number);
            }
        };
    }

    @Override
    public boolean needUpdate() {
        return ResKeyboardInfo.needUpdate();
    }

    private Keyboard localizeKeys(Keyboard keyboard, KeyboardInfo info, String modeLabel) {
        List<Key> keys = keyboard.getKeys();

        for (Key key : keys) {
            if (key.codes[0] == LeanbackKeyboardView.ASCII_SPACE) {
                localizeSpace(key, info);
            } else if (key.codes[0] == LeanbackKeyboardView.KEYCODE_SYM_TOGGLE) {
                // Material style: mode switch key shows text instead of an image
                key.icon = null;
                key.label = modeLabel;
            }
        }

        return keyboard;
    }

    private void localizeSpace(Key key, KeyboardInfo info) {
        if (mCachedSpace.containsKey(info.getLangCode())) {
            key.icon = mCachedSpace.get(info.getLangCode());
            return;
        }

        // Material style: language name on a plain key cap, no space bar image
        TextDrawable drawable = new TextDrawable(mContext);
        drawable.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        //Customize text size and color (color is tinted by the keyboard view)
        drawable.setTextColor(Color.WHITE);
        drawable.setTextSizeFactor(SPACE_TEXT_SIZE_FACTOR);
        drawable.setTextSize(TypedValue.COMPLEX_UNIT_PX, key.height * SPACE_TEXT_SIZE_FACTOR);
        drawable.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        drawable.setText(info.getLangName());
        key.icon = drawable;

        mCachedSpace.put(info.getLangCode(), drawable);
    }
}
