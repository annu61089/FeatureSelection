/**
 * Copyright (C) 2013-2017 Vasilis Vryniotis <bbriniotis@datumbox.com>
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
package com.datumbox.framework.core.common.text.extractors;

import com.datumbox.framework.core.common.utilities.outlier;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This extractor class extracts the unique keywords of a string as a sequence of words.
 * Keywords that already appeared at the beginning of the string, do not reappear 
 * in the returned sequence more than once.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class UniqueWordSequenceExtractor extends AbstractTextExtractor<UniqueWordSequenceExtractor.Parameters, Integer, String> {
    
    /**
     * AbstractParameters of the UniqueWordSequenceExtractor.
     */
    public static class Parameters extends AbstractTextExtractor.AbstractParameters {  
        private static final long serialVersionUID = 1L;
        
    }

    public static List<String> outLayers;
    
    /**
     * Public constructor that accepts as arguments the AbstractParameters object.
     * 
     * @param parameters 
     */
    public UniqueWordSequenceExtractor(Parameters parameters) {
        super(parameters);
        outLayers = new ArrayList<>();
    }

    public UniqueWordSequenceExtractor(Parameters parameters, String fileOutLayers)  {
        super(parameters);
        try {
            if (Files.exists(Paths.get(fileOutLayers))) {
                outLayers = Files.readAllLines(Paths.get(fileOutLayers));
            }
            else {
                outLayers = outlier.getOutLiers(); //Files.readAllLines(Paths.get(fileOutLayers));
            }
        } catch (Exception e) {

        }
    }
    
    /**
     * This method gets as input a string and returns as output a numbered sequence
     * of the unique tokens. In the returned map as keys we store the position of the word
     * in the original string and as value the actual unique token in that position.
     * Note that the sequence includes only the position of the first occurrenceof
     * each word while the next occurrences are ignored.
     * 
     * @param text
     * @return 
     */
    @Override
    public Map<Integer, String> extract(final String text) {
        Set<String> tmpKwd = new LinkedHashSet<>(generateTokenizer().tokenize(text));
        
        Map<Integer, String> keywordSequence = new LinkedHashMap<>();
        
        int position = 0;
        for(String keyword : tmpKwd) {
            if(keyword.length() < 4 || keyword.contains("@") || keyword.contains(".")
                    || outLayers.contains(keyword.toLowerCase()))
            {
                continue;
            }
            keywordSequence.put(position, keyword);
            ++position;
        }
        
        return keywordSequence;
    }
   
}
