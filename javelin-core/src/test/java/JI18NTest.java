/// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
// */
//
//import com.github.paohaijiao.enums.JResourceType;
//import com.github.paohaijiao.i18n.JI18nService;
//import com.github.paohaijiao.i18n.factory.JI18nFactory;
//import org.junit.Test;
//
//
//
///**
// * packageName PACKAGE_NAME
// *
// * @author Martin
// * @version 1.0.0
// * @className JI18NTest
// * @date 2025/6/19
// * @description
// */
//public class JI18NTest {
//
//    @Test
//    public void testI18N() throws IOException {
//        JI18nService enService = JI18nFactory.getI18nService("en");
//        JI18nService zhService = JI18nFactory.getI18nService("zh");
//
//        System.out.println(enService.getMessage("welcome"));
//        System.out.println(zhService.getMessage("welcome"));
//        System.out.println(enService.getMessage("greeting", "John"));
//
//
//        JI18nService enYamlService = JI18nFactory.getI18nService("en", JResourceType.YAML);
//        System.out.println(enYamlService.getMessage("error.not_found"));
//    }
//}
