/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList;
    private HashSet<String> wordSet;
    private HashMap<String, ArrayList<String>> lettersToWord;
    private HashMap<Integer, ArrayList<String>> sizeToWords;
    private int wordLength;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        wordList = new ArrayList<>();
        wordSet = new HashSet<>();
        lettersToWord = new HashMap<>();
        sizeToWords = new HashMap<>();
        wordLength = DEFAULT_WORD_LENGTH;
        String line;

        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);

            int length = word.length();
            if (sizeToWords.containsKey(length)) {
                sizeToWords.get(length).add(word);
            } else {
                ArrayList<String> arr = new ArrayList<>();
                arr.add(word);
                sizeToWords.put(length, arr);
            }

            String sortedWord = sortLetters(word);
            if (lettersToWord.containsKey(sortedWord)) {
                lettersToWord.get(sortedWord).add(word);
            } else {
                ArrayList<String> arr = new ArrayList<>();
                arr.add(word);
                lettersToWord.put(sortedWord, arr);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.toLowerCase().contains(base.toLowerCase());
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<>();
        String sortedWord = sortLetters(targetWord);
        for (String s: wordList) {
            if (sortLetters(s).equals(sortedWord)) {
                result.add(s);
            }
        }
        return result;
    }

    private String sortLetters(String s) {
        s = s.toLowerCase();
        char[] c = s.toCharArray();
        Arrays.sort(c);
        return new String(c);
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<>();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            String s = sortLetters(word + letter);
            int i=0;
            while(lettersToWord.containsKey(s) && i<lettersToWord.get(s).size()) {
                if (isGoodWord(lettersToWord.get(s).get(i),word))
                    result.add(lettersToWord.get(s).get(i));
                i++;
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        ArrayList<String> wordListSize = sizeToWords.get(wordLength);
        int size = wordListSize.size();
        int n = random.nextInt(size);
        for (int i = n; i < n + size; i++) {
            String word = wordListSize.get(i % size);
            ArrayList<String> arr = lettersToWord.get(sortLetters(word));
            if (arr.size() >= MIN_NUM_ANAGRAMS && wordLength <= MAX_WORD_LENGTH) {
                wordLength++;
                return word;
            }
        }
        wordLength++;
        return "stop";
    }
}