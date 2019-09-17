/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.converter;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.json.JsonObjectBuilder;

@Component
class DirectDateTypeConverter extends BaseTypeConverter {
  private static final String[] DATE_FORMATS = new String[]{
      "yyyy-M-d", "yy-M-d", "d/M/yyyy", "d/M/yy", "dd/MM/yyyy"
  };

  @Override
  public boolean supports(String type) {
    return "DIRECT_DATE".equalsIgnoreCase(type);
  }

  @Override
  public void convert(JsonObjectBuilder builder, Mapping mapping, String value) {
    if (isBlank(value) || "null".equals(value)) {
      logger.debug("Blank/null value for field: {}", mapping.getTo());
      return;
    }

    String date = parseDate(value);

    if (null == date || "null".equals(date)) {
      logger.debug("Can't parse date {} for field: {}", value, mapping.getTo());
      return;
    }

    builder.add(mapping.getTo(), date);
  }

  private String parseDate(String value) {
    LocalDate date = null;

    for (String format : DATE_FORMATS) {
      try {
        date = LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
        break;
      } catch (DateTimeParseException exp) {
        date = null;
        logger.debug("Can't parse date {} with format {}", value, format, exp);
      }
    }

    return null == date ? null : date.format(DateTimeFormatter.ISO_DATE);
  }

}
