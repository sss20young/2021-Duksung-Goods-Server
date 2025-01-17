package com.example.duksunggoodsserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/chat-rooms")
public class ChatRoomViewController {

    @GetMapping("")
    public String chatRoomList() {
        return "chatroom";
    }

    @GetMapping("/{roomUUID}")
    public String roomDetail(@PathVariable String roomUUID, Model model) {
        model.addAttribute("roomUUID", roomUUID);
        return "roomdetail";
    }
}
