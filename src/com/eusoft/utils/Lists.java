/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eusoft.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Provides static methods for creating {@code List} instances easily, and other
 * utility methods for working with lists.
 */
public class Lists {

    /**
     * Creates an empty {@code ArrayList} instance.
     * <p/>
     * <p><b>Note:</b> if you only need an <i>immutable</i> empty List, use
     * {@link java.util.Collections#emptyList} instead.
     *
     * @return a newly-created, initially-empty {@code ArrayList}
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    /**
     * Creates a resizable {@code ArrayList} instance containing the given
     * elements.
     * <p/>
     * <p><b>Note:</b> due to a bug in javac 1.5.0_06, we cannot support the
     * following:
     * <p/>
     * <p>{@code List<Base> list = Lists.newArrayList(sub1, sub2);}
     * <p/>
     * <p>where {@code sub1} and {@code sub2} are references to subtypes of
     * {@code Base}, not of {@code Base} itself. To get around this, you must
     * use:
     * <p/>
     * <p>{@code List<Base> list = Lists.<Base>newArrayList(sub1, sub2);}
     *
     * @param elements the elements that the list should contain, in order
     * @return a newly-created {@code ArrayList} containing those elements
     */
    public static <E> ArrayList<E> newArrayList(E... elements) {
        int capacity = (elements.length * 110) / 100 + 5;
        ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * save a list to local SharedPreferences
     *
     * @param context    current context
     * @param preKey     key
     * @param dataSource list<?>
     */
    public static void saveListToSharedPreferences(Context context, String preKey, ArrayList<?> dataSource) {
        try {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.remove(preKey).commit();
            String serialized = PreferenceManager.getDefaultSharedPreferences(context).getString(preKey, "");
            Log.e("TAG", serialized + " ------- " + TextUtils.join(",", dataSource));
            editor.putString(preKey, TextUtils.join(",", dataSource)).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * load list from SharedPreferences
     *
     * @param context current context
     * @param preKey  key
     * @return
     */
    public static <T> ArrayList<T> loadListFromSharedPreferences(Context context, String preKey) {
        ArrayList<T> temp = newArrayList();
        try {
            String serialized = PreferenceManager.getDefaultSharedPreferences(context).getString(preKey, "");
            //Changing this one to linkedlist should solve the java.lang.UnsupportedOperationException problem
            LinkedList myList = new LinkedList(Arrays.asList(TextUtils.split(serialized, ",")));
            for (Object s : myList) {
                temp.add((T) s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    static <T> ArrayList<T> arrayToList(final T[] array) {
        final ArrayList<T> l = new ArrayList<T>(array.length);

        for (final T s : array) {
            l.add(s);
        }
        return (l);
    }



    public static boolean isValidate(Collection<?> list){
        return list != null && list.size() > 0;
    }
}
