package org.example.adventuretime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     * Отдаёт index.html для всех «глубоких» GET‑запросов,
     * кроме тех, что начинаются с /api или имеют точку (ресурсы).
     */
    @GetMapping({
            "/",                        // корень
            "/{path:[^\\.]+}",          // один сегмент без точки
            "/{path:^(?!api$).+}/{sub:[^\\.]+}"  // два сегмента, первый не "api", второй без точки
    })
    public String forward() {
        return "forward:/index.html";
    }
}
