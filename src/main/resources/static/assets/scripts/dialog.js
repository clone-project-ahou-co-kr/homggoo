export class Dialog {
    /**
     * @type {HTMLElement};
     */
    #$element;
    /**
     * @type {HTMLElement[]};
     */
    #$modals = [];

    /**
     *
     * @param {{$element:HTMLElement}}args
     */
    constructor(args) {

        let $element = document.body.querySelector('.dialog');
        if ($element === null) {
            $element = document.createElement('div');
            $element.classList.add('dialog');
            document.body.insertAdjacentElement('afterbegin', $element);
        }
        this.#$element = $element;
    }

    /**
     *
     * @param {HTMLElement}$modal
     */
    hide = ($modal) => {
        const index = this.#$modals.indexOf($modal);
        if (index < 0) {
            return;
        }
        this.#$modals.splice(index, 1);
        if (this.#$modals.length === 0) {
            this.#$element.setVisible(false);
        }
        $modal.setVisible(false);
        setTimeout(() => $modal.remove(), 1000);
    }
    /**
     *
     * @param{{
     *     title:string,
     *     content:string, isContentHtml?:boolean,delay?:number,buttons:{
     *         caption:string,
     *         color:'green'|'gray'|'red',
     *         onClickCallback:function(HTMLElement)
     *     }[]
     * }} args
     */
    show = (args) => {
        const $modal = document.createElement('div');
        $modal.classList.add('modal');
        const $title = document.createElement('div');
        $title.classList.add('title');//->$title.dataset['mtComponent'] = 'dialog.modal.title';
        $title.innerText = args.title;
        const $content = document.createElement('div');
        $content.classList.add('content');
        if (args.isContentHtml === true) {
            $content.innerHTML = args.content;
        } else {
            $content.innerText = args.content;
        }
        const $buttonContainer = document.createElement('div');
        $buttonContainer.classList.add('button-container');
        $buttonContainer.append(...args.buttons.map((button) => {
            const $button = document.createElement('button');
            $button.addEventListener('click', () => button.onClickCallback($modal));
            $button.setAttribute('type', 'button');
            $button.classList.add('button');
            $button.classList.add(button.color ?? 'gray');
            $button.innerText = button.caption;
            return $button;
        }));
        $modal.append($title, $content, $buttonContainer);
        this.#$element.append($modal);
        this.#$element.setVisible(true);
        this.#$modals.push($modal);
        setTimeout(() => {
            $modal.setVisible(true)
        }, args.delay ?? 25);
        return $modal;
    }
    /**
     *
     * @param{string} title
     * @param{string} content
     * @param {{delay?:number,isContentHtml?:boolean, onClickCallback?:function(HTMLElement)}?}args
     */
    showSimpleOk = (title, content, args = {}) => {
        return this.show({
            title: title,
            content: content,
            delay: args?.delay,
            isContentHtml: args?.isContentHtml,
            buttons: [
                {
                    caption: '확인',
                    color: 'green',
                    onClickCallback: ($modal) => {
                        this.hide($modal);
                        args?.onClickCallback?.($modal); //~?.()는 선행하는 피연산자가 있으면 호출하겠다는 의미
                        //if(typeof args?.onClickCallback==='function'){
                        //  args.onClickCallback($modal);
                        //}
                        //랑 유사한 효과
                    }
                }
            ]
        })
    }
}











