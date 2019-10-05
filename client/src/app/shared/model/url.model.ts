export class UrlModel {
    id:string;
    shortName: string;
    targetURL: string;
    owner:string;
    clickCount:number;
    expirationDate:number;
    effectiveDate:number;

    constructor() {
        this.id = this.uuidv4();
        console.log(this.id);
    }
    uuidv4() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

}
