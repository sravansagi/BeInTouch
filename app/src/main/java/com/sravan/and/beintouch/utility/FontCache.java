package com.sravan.and.beintouch.utility;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by Sravan on 6/6/2017.
 */

/**
 * The FontCache class is used to cache the fonts to overcome the memory leak problem mentioned in the
 * following stackover flow link https://stackoverflow.com/questions/16901930/memory-leaks-with-custom-font-for-set-custom-font
 */
public class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    /**
     * get method is used to return the custom font by caching the font in the hashmap
     * @param name The name of the font
     * @param context The associated application context
     * @return Typeface of the requested font
     */
    public static Typeface get(String name, Context context) {
        Typeface typeface = fontCache.get(name);
        if(typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, typeface);
        }
        return typeface;
    }
}