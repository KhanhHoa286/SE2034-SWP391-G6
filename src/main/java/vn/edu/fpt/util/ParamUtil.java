package vn.edu.fpt.util;


import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * HoaNK - HE195013
 * Date:
 * Description: partse parameter
 */
public class ParamUtil {
        // parse Integer
        public static Integer getInteger(HttpServletRequest request, String paramName){
            String value = request.getParameter(paramName);
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // parse BigDecimal
        public static BigDecimal getBigDecimal(HttpServletRequest request, String paramName) {
            String value = request.getParameter(paramName);
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(value.trim());
            } catch (Exception e) {
                return null;
            }
        }

        // parse String
        public static String getString(HttpServletRequest request, String paramName, String defaultValue) {
            String value = request.getParameter(paramName);
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return value.trim();
        }
}
