/**
 * RUTHRAMART - AI CHATBOT WIDGET
 */

const chatbot = {
    isOpen: false,

    init() {
        this.injectChatbotUI();
        this.setupEventListeners();
    },

    injectChatbotUI() {
        if (document.getElementById('ruthramart-chatbot')) return;

        const widgetContainer = document.createElement('div');
        widgetContainer.id = 'ruthramart-chatbot';
        widgetContainer.innerHTML = `
            <!-- Chatbot Toggle Button -->
            <button class="chatbot-toggler" id="chatbot-toggle-btn" title="Chat with RuthraMart AI">
                <i class="fa-solid fa-wand-magic-sparkles"></i>
            </button>

            <!-- Chatbot Window -->
            <div class="chatbot-widget" id="chatbot-window">
                <div class="chatbot-header">
                    <div class="chatbot-header-info">
                        <i class="fa-solid fa-robot" style="font-size: 1.3rem; color: var(--light);"></i>
                        <div>
                            <h4>RuthraMart AI</h4>
                            <span>Online • Shopping Assistant</span>
                        </div>
                    </div>
                    <button class="chatbot-close" id="chatbot-close-btn">&times;</button>
                </div>
                <div class="chatbot-messages" id="chatbot-messages">
                    <div class="chat-bubble bot">
                        Hi! I'm RuthraMart AI Assistant. 🌿
                        How can I help you today?
                    </div>
                    <div style="display: flex; gap: 6px; flex-wrap: wrap; margin-top: 6px;" id="chatbot-quick-chips">
                        <button class="btn btn-outline btn-sm" style="font-size: 0.75rem; padding: 4px 10px; border-radius: var(--radius-full);" onclick="chatbot.sendQuickMessage('I need a laptop')">💻 Laptop</button>
                        <button class="btn btn-outline btn-sm" style="font-size: 0.75rem; padding: 4px 10px; border-radius: var(--radius-full);" onclick="chatbot.sendQuickMessage('What categories do you have?')">📂 Categories</button>
                        <button class="btn btn-outline btn-sm" style="font-size: 0.75rem; padding: 4px 10px; border-radius: var(--radius-full);" onclick="chatbot.sendQuickMessage('How can I add products to my cart?')">🛒 Cart Help</button>
                        <button class="btn btn-outline btn-sm" style="font-size: 0.75rem; padding: 4px 10px; border-radius: var(--radius-full);" onclick="chatbot.sendQuickMessage('Where can I see my orders?')">📦 Track Orders</button>
                    </div>
                </div>
                <form class="chatbot-input-area" id="chatbot-form">
                    <input type="text" id="chatbot-input" placeholder="Ask about products, orders, categories..." autocomplete="off">
                    <button type="submit" id="chatbot-send-btn"><i class="fa-solid fa-paper-plane"></i></button>
                </form>
            </div>
        `;
        document.body.appendChild(widgetContainer);
    },

    setupEventListeners() {
        const toggleBtn = document.getElementById('chatbot-toggle-btn');
        const closeBtn = document.getElementById('chatbot-close-btn');
        const form = document.getElementById('chatbot-form');

        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => this.toggleChat());
        }
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.toggleChat());
        }
        if (form) {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleUserSubmit();
            });
        }
    },

    toggleChat() {
        this.isOpen = !this.isOpen;
        const windowEl = document.getElementById('chatbot-window');
        if (windowEl) {
            windowEl.classList.toggle('active', this.isOpen);
            if (this.isOpen) {
                const input = document.getElementById('chatbot-input');
                if (input) input.focus();
            }
        }
    },

    sendQuickMessage(msg) {
        const input = document.getElementById('chatbot-input');
        if (input) {
            input.value = msg;
            this.handleUserSubmit();
        }
    },

    async handleUserSubmit() {
        const input = document.getElementById('chatbot-input');
        if (!input) return;
        const message = input.value.trim();
        if (!message) return;

        input.value = '';
        this.appendMessage(message, 'user');

        const messagesContainer = document.getElementById('chatbot-messages');
        const typingIndicator = document.createElement('div');
        typingIndicator.className = 'chat-bubble bot';
        typingIndicator.id = 'chat-typing-indicator';
        typingIndicator.innerHTML = '<i class="fa-solid fa-ellipsis fa-fade"></i> Thinking...';
        messagesContainer.appendChild(typingIndicator);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;

        try {
            const data = await api.post('/chatbot', { message });
            typingIndicator.remove();
            this.appendMessage(data.reply || 'I am here to help you shop seamlessly on RuthraMart!', 'bot');
        } catch (e) {
            typingIndicator.remove();
            this.appendMessage('I am having a brief connection delay. Please feel free to explore our Products or Categories from the top menu!', 'bot');
        }
    },

    appendMessage(text, sender) {
        const messagesContainer = document.getElementById('chatbot-messages');
        if (!messagesContainer) return;

        const bubble = document.createElement('div');
        bubble.className = `chat-bubble ${sender}`;
        bubble.textContent = text;
        messagesContainer.appendChild(bubble);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
};

document.addEventListener('DOMContentLoaded', () => {
    chatbot.init();
});
