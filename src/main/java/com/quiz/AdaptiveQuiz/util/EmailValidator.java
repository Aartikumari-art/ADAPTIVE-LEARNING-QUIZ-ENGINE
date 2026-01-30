package com.quiz.AdaptiveQuiz.util;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class EmailValidator {

    public static boolean isValidEmailDomain(String email) {
        int pos = email.indexOf('@');
        if (pos == -1)
            return false;

        String domain = email.substring(pos + 1);
        if (domain.isEmpty())
            return false;

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ictx = new InitialDirContext(env);

            Attributes attrs = ictx.getAttributes(domain, new String[] { "MX" });
            Attribute attr = attrs.get("MX");

            // If we have any MX record, it's valid
            return attr != null && attr.size() > 0;

        } catch (NamingException e) {
            // Domain doesn't exist or no MX record
            return false;
        }
    }
}
