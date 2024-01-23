const messages = {
    item: 'item',
    meter: 'meter.',
    liter: 'liter.',
    kg: 'kg.',
    item_sequence: 'item;meter;liter',
    meter_sequence: 'm;meter',
    liter_sequence: 'l;liter',
    kg_sequence: 'kg;kilogram',
    next: 'Next',
    back: 'Back',
    showed: 'showed',
    fromAmong: 'from',
    fromStart: 'From start',
    removeFrom: 'Remove from',
    confirmAction: 'confirm action',
    no: 'no',
    yes: 'yes',
    showBy: 'by',
    selectedRecently: 'selected recently',
    selectedAgo: 'selected ago',
    popular: 'popular',
    cheap: 'cheap',
    expensive: 'expensive',
    new: 'new',
    remove: 'Remove',
    card: 'card',
    origin: 'origin',
    settings: 'settings',
    accounts: 'accounts',
    upload: 'upload',
    uploadFrom: 'upload from',
    upload: 'upload',
    downloadTo: 'download to',
    download: 'download',
    browse: 'browse',
    db: 'database',
    unique: 'unique',
    notUnique: 'not unique',
    dragFilesHereOr: 'drag files here or',
    chooseFiles: 'choose files',
    invalidTypeFile: 'Invalid type of file. Accepted only',
    sizeExceed: 'files should not exceed in size',
    chooseFileWithExtension: 'choose file with extension',
    chooseFilesWithExtension: 'choose files with extension',
    dragFileWithExtension: 'drag file with extension',
    dragFilesWithExtension: 'drag files with extension',
    uploadFiles: 'upload files',
    server: 'server',
    dictionary: 'dictionary',
    editor: 'editor',
    word: 'word',
    translation: 'translation',
    example: 'example',
    exampleTranslation: 'example translation',
    learned: 'learned',
    picture: 'picture',
    audio: 'audio',
    addDictionary: 'add dictionary',
    addVocabulary: 'add vocabulary',
    addCard: 'add card',
    addCards: 'add cards',
    tableSettings: 'table settings',
    propertySettings: 'columns and rows',
    hiddenColumns: 'hidden columns',
    activeColumns: 'active columns',
    enterName: 'enter name',
    enterDictionary: 'enter dictionary',
    enterWord: 'enter word',
    enterTranslation: 'enter translation',
    enterExample: 'enter example',
    enterExampleTranslation: 'enter example translation',
    copied: 'copied',
    ru: 'russian',
    en: 'english',
    applyDefault: 'apply default',
    email: 'email',
    password: 'password',
    passwordRepeat: 'password (repeat)',
    repeatPassword: 'repeat password',
    signUpDo: 'sign up',
    signInDo: 'sign in',
    logoStartDate: '2023',
    enterEmail: 'enter email',
    enterPassword: 'enter password',
    signUpEmailError: 'check entered email address - wrong format',
    signUpPasswordShortLengthError: 'password must not be shorter than 8 characters',
    signUpPasswordLongLengthError: 'password must not be longer than 20 characters',
    signUpPasswordSyntaxError: 'Weak password. Password must contain uppercase letters, lowercase letters, numbers and special characters.',
    signUpPasswordRepeatError: 'Passwords do not coincide. Enter password again',
    signUpPasswordMinCharacters: 'minimum characters: 8',
    signUpPasswordMaxCharacters: 'maximum characters: 20',
    signUpPasswordUppercaseLetter: 'uppercase letter',
    signUpPasswordLowercaseLetter: 'lowercase letter',
    signUpPasswordNumber: 'number',
    signUpPasswordSpecialCharacter: 'special character',
    signIn: 'sign in',
    signUp: 'sign up',
    externalServerError: 'error of external resource',
    cannotGetAnswerFrom: 'cannot get answer from',
    tryToReloadPage: 'try to reload the page',
    checkInternetConnection: 'check internet connection',
    noInternetConnection: 'no internet connection',
    pleaseWaitThreeDot: 'please wait...',
    hide: 'hide',
    close: 'close',
    checkAndConfirmEmail: 'check and confirm your e-mail address',
    onlyWithConfirmedEmailHaveAccess: 'only with confirmed e-mail address you have access to',
    failureOperation: 'failed operation',
    successOperation: 'successful operation',
    success: 'success',
    failure: 'неудача',
    signInFailure: 'wrong email address and/or password',
    pleaseWaitLoading: 'please wait a loading',
    capsLockTurnedOn: 'CAPSLOCK is turned on',
    onlyLatinLetters: 'only latin letters',
    useServices: 'use services:',
    or: 'or',
    account: 'account',
    notifications: 'notifications',
    delete: 'delete',
    forgotPassword: 'forgot password',
    contacts: 'contacts',
    aboutUs: 'about us',
    vocabulary: 'vocabulary',
    selected: 'selected',
    selectAll: 'select all',
    deselectAll: 'deselect all',
    details: 'details',
    logout: 'logout',
    otherAccount: 'other account',
    currentAccount: 'current account',
    absent: 'absent',
    chooseDictionary: 'choose dictionary to see its content',
    sourceLang: 'source language',
    targetLang: 'target language',
    options: 'options',
    saveNewUnrepeatedCards: 'save new unrepeated',
    saveAllUploadCards: 'save all upload',
    updateLearnedStatusUnrepeatedCards: 'update learned status unrepeated',
    updateCardsWithAbsentSound: 'update cards with absent sound',
    selectOption: 'select option',
    createDb: 'create database',
    create: 'create',
    welcomeTo: 'welcome to',
    name: 'name',
    dangerZone: 'danger zone',
    deleteVocabulary: 'delete vocabulary',
    input: 'input',
    deleteDictionaries: 'delete dictionaries',
    enterGroup: 'enter group',
    group: 'group',
    enterVocabularyName: 'enter vocabulary name',
    confirmDeleteDictionary: 'delete the dictionary without recovery?',
    confirmDeleteCards: 'delete cards without recovery?',
    accessAllowed: 'access allowed',
    accessDenied: 'access denied',
    conditions: 'conditions',
    confidence: 'confidence',
    contactUs: 'contact us',
    media: 'media',
    listEmpty: 'list is empty',
    fileType: 'file type',
    sheet: 'sheet',
    target: 'target',
    source: 'source',
    size: 'size',
    max: 'max',
    files: 'files',
    extension: 'extension',
    list: 'list',
    grid: 'grid',
    removeAll: 'remove all',
    removeInvalid: 'remove invalid',
    waitForAction: 'wait for action',
    fixFilesWithError: 'fix files with error',
    needRemoveInvalid: 'need remove invalid',
    readyForUpLoad: 'ready for upload',
    uploadFileNote: 'make sure the file format matches any downloading file',
    uploadCardNote: 'all text fields exceeding the length will be truncated',
    continue: 'continue',
    report: 'report',
    countUploadedFiles: 'count uploaded files',
    countNotUploadedFiles: 'count not uploaded files',
    countUploadedCards: 'count uploaded cards',
    countNotUploadedCards: 'count not uploaded cards',
    requirements: 'requirements',
    all: 'all',
    cannotDeselect: 'cannot deselect',
    enterType: 'enter type',
    enterLang: 'enter language',
    nothingFound: 'Oops! Nothing found.',
    loaded: 'loaded',
    notLoaded: 'not loaded',
    creationLDT: 'creation date',
    datepickerLabelHelp: 'use cursor keys to navigate calendar dates',
    showPassword: 'show password',
    hidePassword: 'hide password',


    dictionaryNotUniqueError: 'Dictionary already exists. Change name or parent.',
    cardNotUniqueError: 'Card already exists. Change word or translation.',
    vocabularyNotUniqueError: 'Vocabulary already exists. Change name or language.',
    incorrectInputError: 'Incorrect input.',
}
module.exports = messages
