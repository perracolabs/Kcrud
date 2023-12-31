/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.env.security.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil

/**
 * Utility class for security related operations.
 */
object SecurityUtils {

    /**
     * Verifies if an email address is in the correct format.
     * It adheres to the RFC 5321 specification, which defines the standard format for email addresses.
     *
     * The top-level domain (TLD) must be at least two characters long.
     * The total length of the email address must not exceed 254 characters (as per RFC 5321).
     *
     * The local part of the email (before the '@') allows:
     * ```
     *      • Uppercase and lowercase letters (A-Z, a-z)
     *      • Digits (0-9)
     *      • Characters: dot (.), underscore (_), percent (%), plus (+), hyphen (-)
     *      • Maximum length of 64 characters (as per RFC 5321)
     * ```
     * The domain part of the email (after the '@') can include:
     * ```
     *      • Letters (A-Z, a-z)
     *      • Digits (0-9)
     *      • Hyphens (-)
     * ```
     *
     * Examples of valid email formats:
     * ```
     *      • example@email.com
     *      • user.name+tag+sorting@example.co.uk
     *      • user_name@example.org
     *      • username@example.travel
     *      • user1234@example-company.com
     * ```
     * Examples of invalid email formats:
     * ```
     *      • any-plain-text
     *      • @no-local-part.com
     *      • .email@example.com (local part starts with a dot)
     *      • email.@example.com (local part ends with a dot)
     *      • email..email@example.com (local part has consecutive dots)
     *      • email@example (no top-level domain)
     *      • email@example...com (top-level domain has consecutive dots)
     * ```
     *
     * @param email The email address to check.
     * @return True if the email is valid according to the regex pattern, false otherwise.
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
        if (!email.matches(emailRegex.toRegex())) {
            return false
        }

        // Check for the maximum length of the entire email address (254 characters).
        if (email.length > 254) {
            return false
        }

        // Splitting local and domain parts to apply specific checks.
        val parts = email.split("@")
        val localPart = parts[0]
        val domainPart = parts[1]

        // Check for the maximum length of the local part (64 characters).
        if (localPart.length > 64) {
            return false
        }

        // Ensure domain part does not have consecutive dots.
        if (domainPart.contains("..")) {
            return false
        }

        // Check if the local part starts or ends with a dot, or contains consecutive dots.
        if (localPart.startsWith(".") || localPart.endsWith(".") || localPart.contains("..")) {
            return false
        }

        return true
    }

    /**
     * Verifies if a phone number is in the correct format.
     * @param phone The phone number to check.
     * @return True if the phone number is valid, false otherwise.
     */
    fun isValidPhone(phone: String): Boolean {
        val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
        val numberProto = phoneUtil.parse(phone, null) // Region code is null for international numbers.
        return phoneUtil.isValidNumber(numberProto)
    }

    /**
     * Escapes HTML tags in a string to prevent XSS (Cross-Site Scripting) attacks.
     *
     * This function replaces all instances of '<' with '&lt;' and '>' with '&gt;' in the input string.
     * This conversion ensures that any HTML tags present in the input are not rendered by the browser,
     * preventing potential XSS attacks where malicious scripts could be injected and executed.
     *
     * Note: This method is basic and might not cover all the cases for XSS prevention. For more
     * comprehensive security, consider using established libraries for input sanitization.
     *
     * Examples:
     * ```
     *      Input: "<script>alert('XSS')</script>"
     *      Output: "&lt;script&gt;alert('XSS')&lt;/script&gt;"
     *      Explanation: Converts script tags into harmless text.
     *
     *      Input: "<b>Hello, World!</b>"
     *      Output: "&lt;b&gt;Hello, World!&lt;/b&gt;"
     *      Explanation: Converts bold tags into text, preventing any HTML rendering.
     *
     *      Input: "Normal text without HTML"
     *      Output: "Normal text without HTML"
     *      Explanation: Text without HTML tags remains unchanged.
     *```
     * @param input The string to sanitize.
     * @return The sanitized string with HTML tags escaped.
     */
    @Suppress("unused")
    fun sanitizeInput(input: String): String {
        return input.replace("<", "&lt;").replace(">", "&gt;")
    }
}