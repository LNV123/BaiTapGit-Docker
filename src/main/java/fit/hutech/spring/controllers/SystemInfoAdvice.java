package fit.hutech.spring.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.net.InetAddress;
import java.net.UnknownHostException;

@ControllerAdvice
public class SystemInfoAdvice {

    @ModelAttribute
    public void addSystemInfo(Model model) {
        String containerId = "Unknown";
        String ipAddress = "Unknown";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            containerId = localHost.getHostName();
            ipAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            // Handle cases where hostname cannot be resolved
        }

        // Add these to the model so they are available in every template (like layout.html)
        model.addAttribute("dockerContainerId", containerId);
        model.addAttribute("dockerIp", ipAddress);
        model.addAttribute("dockerImage", "bookstore-app:latest"); // Can be set via env if needed
        model.addAttribute("dockerPort", "8081");
    }
}
