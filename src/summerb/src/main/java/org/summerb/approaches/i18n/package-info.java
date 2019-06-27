/**
 * This package contains core design and impl for i18n aspect of the
 * application.
 * 
 * The idea is that back-end will never (or almost never) deal with user-facing
 * text. Instead it will operate using messageCodes (constants) which then can
 * be translated to a human-readable form upon presentation/rendering OR used
 * for actual logic (since these are constants it's pretty reliable).
 * 
 * Also each message can have number of
 * {@link org.summerb.approaches.i18n.HasMessageArgs} array. This helps to
 * transport machine-readable data (you don't need to parse it) and again easily
 * use it to translate whole message to human-readable form.
 * 
 * @author sergeyk
 *
 */
package org.summerb.approaches.i18n;