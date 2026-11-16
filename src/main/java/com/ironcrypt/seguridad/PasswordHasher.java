/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author santiagogrillo
 */

package com.ironcrypt.seguridad;
import org.mindrot.jbcrypt.BCrypt;


public class PasswordHasher {
      /**
      * Genera un hash seguro BCrypt con salt aleatorio (costo por defecto 10).         
      */
     public static String hash(String plainPassword) {
         return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
     }
     
    
     public static boolean check(String plainPassword, String hashedPassword) {         
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
