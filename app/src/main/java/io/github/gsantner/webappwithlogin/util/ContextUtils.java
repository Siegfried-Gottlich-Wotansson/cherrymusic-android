/*
 * ---------------------------------------------------------------------------- *
 * Gregor Santner <gsantner.github.io> wrote this file. You can do whatever
 * you want with this stuff. If we meet some day, and you think this stuff is
 * worth it, you can buy me a coke in return. Provided as is without any kind
 * of warranty. No attribution required.                  - Gregor Santner
 *
 * License of this file: Creative Commons Zero (CC0 1.0)
 *  http://creativecommons.org/publicdomain/zero/1.0/
 * ----------------------------------------------------------------------------
 */
package io.github.gsantner.webappwithlogin.util;

import android.content.Context;

import io.github.gsantner.webappwithlogin.App;

@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "SpellCheckingInspection"})
public class ContextUtils extends net.gsantner.opoc.util.ContextUtils {
    public ContextUtils(Context context) {
        super(context);
    }

    public static ContextUtils get() {
        return new ContextUtils(App.get());
    }
}
