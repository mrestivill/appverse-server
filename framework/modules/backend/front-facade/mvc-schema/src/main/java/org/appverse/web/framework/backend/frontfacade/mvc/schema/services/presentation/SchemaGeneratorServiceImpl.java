/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the conditions of the AppVerse Public License v2.0
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */
package org.appverse.web.framework.backend.frontfacade.mvc.schema.services.presentation;
import org.codehaus.jackson.map.ObjectMapper;
import org.appverse.web.framework.backend.core.exceptions.PresentationException;

import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.rest.webmvc.json.JsonSchema;
import org.springframework.data.rest.webmvc.json.PersistentEntityToJsonSchemaConverter;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RestController
@ConditionalOnProperty(value="appverse.frontfacade.rest.schema.enabled", matchIfMissing=false)
@RequestMapping(value = "${appverse.frontfacade.rest.api.basepath:/api}")
public class SchemaGeneratorServiceImpl {
    @Autowired
    private PersistentEntityToJsonSchemaConverter entityConverter;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listEntities(){
        List<String> data = new ArrayList<String>();
        Set<GenericConverter.ConvertiblePair> list =  entityConverter.getConvertibleTypes();
        for (GenericConverter.ConvertiblePair element: list){
            if (!Modifier.isAbstract(element.getSourceType().getModifiers())){
                data.add(element.getSourceType().getCanonicalName());
            }
        }
        return data;
    }
    @RequestMapping(value = "/entity/{entity}/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String generateSchemaByEntityName (@PathVariable("entity") String entity) {
        String data = "";
        if (StringUtils.isEmpty(entity)){
            throw new PresentationException("invalid content");
        }
        try {
            Class<?> clazz = Class.forName(entity);
            JsonSchema schema = entityConverter.convert(clazz, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(JsonSchema.class));

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
            mapper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
            mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
            data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);

        }catch (ClassNotFoundException nsme){
            throw new PresentationException("invalid class:"+entity,nsme);
        }catch( IOException e){
            throw new PresentationException("schema generation exception",e);
        }
        return data;
    }


}