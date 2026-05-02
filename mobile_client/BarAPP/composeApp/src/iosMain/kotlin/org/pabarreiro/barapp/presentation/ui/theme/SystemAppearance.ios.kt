package org.pabarreiro.barapp.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIStatusBarStyleDefault
import platform.UIKit.setStatusBarStyle

@Composable
actual fun SystemAppearance(isDark: Boolean) {
    LaunchedEffect(isDark) {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (isDark) UIStatusBarStyleLightContent else UIStatusBarStyleDefault
        )
    }
}
