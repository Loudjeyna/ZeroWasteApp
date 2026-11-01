package com.zerowaste.app.util

import java.util.Locale

/**
 * NumberUtils — دوال مساعدة لعرض الأرقام بصيغة لاتينية (1, 2, 3)
 * في كلتا اللغتين العربية والإنجليزية
 */
object NumberUtils {

    /**
     * تنسيق رقم بصيغة لاتينية (1, 2, 3) بدلاً من العربية (١، ٢، ٣)
     */
    fun formatInt(value: Int): String {
        return String.format(Locale.US, "%d", value)
    }

    /**
     * تنسيق زوج من الأرقام (مثل الكمية المتبقية/الأصلية: 3/5)
     */
    fun formatPair(remaining: Int, total: Int): String {
        return String.format(Locale.US, "%d/%d", remaining, total)
    }

    /**
     * تحويل أي نص يحوي أرقاماً عربية إلى لاتينية
     */
    fun toLatin(input: String): String {
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val result = StringBuilder()
        for (c in input) {
            val idx = arabicDigits.indexOf(c)
            if (idx >= 0) result.append(('0' + idx))
            else result.append(c)
        }
        return result.toString()
    }
}
