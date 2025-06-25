// import {Dialog} from "./dialog";

HTMLElement.VISIBLE_CLASS = 'visible';
HTMLElement.SELECTED_CLASS = 'selected';
HTMLElement.INVALID = 'invalid';
HTMLElement.VALID = 'valid';


/**
 *@returns{boolean}
 */
HTMLElement.prototype.isValid = function () {
    return this.hasAttribute(HTMLElement.VALID);
};
/**
 * @param{boolean} b
 * @returns{HTMLElement}
 */
HTMLElement.prototype.setInvalid = function (b) {
    if (b === true) {
        this.setAttribute(HTMLElement.INVALID, '');
    } else if (b === false) {
        this.removeAttribute(HTMLElement.INVALID);
    }
    return this;
}

/**
 * @param{boolean} b
 * @returns {HTMLElement}
 */
HTMLElement.prototype.setVisible=function(b){
    if (b === true) {
        this.classList.add(HTMLElement.VISIBLE_CLASS);
    }else if (b === false) {
        this.classList.remove(HTMLElement.VISIBLE_CLASS)
    }
    return this;
}

/**
 *
 * @returns {HTMLElement}
 */
HTMLElement.prototype.getWarning=function(){
    return this.querySelector(':scope>.warning');
}

window.dialog = Dialog;