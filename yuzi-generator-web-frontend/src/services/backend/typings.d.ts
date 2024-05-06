declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseGeneratorVO_ = {
    code?: number;
    data?: GeneratorVO;
    message?: string;
  };

  type BaseResponseInt_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponsePageGenerator_ = {
    code?: number;
    data?: PageGenerator_;
    message?: string;
  };

  type BaseResponsePageGeneratorVO_ = {
    code?: number;
    data?: PageGeneratorVO_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type cacheGeneratorUsingGETParams = {
    /** id */
    id?: number;
  };

  type DeleteRequest = {
    id?: number;
  };

  type downloadGeneratorByIdUsingGETParams = {
    /** id */
    id?: number;
  };


  type FileConfigDTO = {
    files?: FileInfoDTO[];
    inputRootPath?: string;
    outputRootPath?: string;
    sourceRootPath?: string;
    type?: string;
  };

  type FileInfoDTO = {
    condition?: string;
    files?: FileInfoDTO[];
    generateType?: string;
    groupKey?: string;
    groupName?: string;
    inputPath?: string;
    outputPath?: string;
    type?: string;
  };

  type Generator = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    distPath?: string;
    favourNum?: number;
    fileConfig?: FileConfigDTO;
    forcedInteractiveSwitch?: boolean;
    id?: number;
    isDelete?: number;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    thumbNum?: number;
    updateTime?: string;
    userId?: number;
    version?: string;
    versionControl?: boolean;
  };

  type GeneratorAddRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfigDTO;
    forcedInteractiveSwitch?: boolean;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    tags?: string[];
    version?: string;
    versionControl?: boolean;
  };


  type getCaptchaUsingGETParams = {
    emailAccount?: string;
  };
  type GeneratorEditRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfigDTO;
    forcedInteractiveSwitch?: boolean;
    id?: number;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    tags?: string[];
    version?: string;
    versionControl?: boolean;
  };

  type GeneratorFavourAddRequest = {
    generatorId?: number;
  };

  type GeneratorFavourQueryRequest = {
    current?: number;
    generatorQueryRequest?: GeneratorQueryRequest;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type GeneratorMakeRequest = {
    meta?: Meta;
    zipFilePath?: string;
  };

  type GeneratorQueryRequest = {
    content?: string;
    current?: number;
    id?: number;
    orTags?: string[];
    pageSize?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    tags?: string[];
    title?: string;
    userId?: number;
  };

  type GeneratorThumbAddRequest = {
    generatorId?: number;
  };

  type GeneratorUpdateRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfigDTO;
    forcedInteractiveSwitch?: boolean;
    id?: number;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    version?: string;
    versionControl?: boolean;
  };

  type GeneratorUseRequest = {
    dataModel?: Record<string, any>;
    id?: number;
  };

  type GeneratorVO = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    distPath?: string;
    favourNum?: number;
    fileConfig?: FileConfigDTO;
    forcedInteractiveSwitch?: boolean;
    hasFavour?: boolean;
    hasThumb?: boolean;
    id?: number;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    thumbNum?: number;
    updateTime?: string;
    user?: UserVO;
    version?: string;
    versionControl?: boolean;
  };

  type getGeneratorVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type LoginUserVO = {
    createTime?: string;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type Meta = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    fileConfig?: FileConfigDTO;
    forcedInteractiveSwitch?: boolean;
    modelConfig?: ModelConfig;
    name?: string;
    version?: string;
    versionControl?: boolean;
  };

  type ModelConfig = {
    models?: ModelInfo[];
  };

  type ModelInfo = {
    abbr?: string;
    allArgsStr?: string;
    condition?: string;
    defaultValue?: Record<string, any>;
    description?: string;
    fieldName?: string;
    groupKey?: string;
    groupName?: string;
    models?: ModelInfo[];
    type?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageGenerator_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: Generator[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageGeneratorVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: GeneratorVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type testDownloadFileUsingGETParams = {
    /** filepath */
    filepath?: string;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type User = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    code?: string;
    userAccount?: string;
    userEmail?: string;
    userPassword?: string;
  };

  type UserUpdateMyRequest = {
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    balance?: number;
    gender?: string;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
    status?: number;
    userAccount?: string;
    userPassword?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
    balance?: number;
    email?: string;
    gender?: string;
    invitationCode?: string;
    status?: number;
    userAccount?: string;
  };
  type UserBindEmailRequest = {
    captcha?: string;
    emailAccount?: string;
  };
  type UserUnBindEmailRequest = {
    captcha?: string;
    emailAccount?: string;
  };
  type getUserByInvitationCodeUsingPOSTParams = {
    /** invitationCode */
    invitationCode?: string;
  };
  type UserEmailRegisterRequest = {
    agreeToAnAgreement?: string;
    captcha?: string;
    emailAccount?: string;
    invitationCode?: string;
    userName?: string;
  };
  type UserEmailLoginRequest = {
    captcha?: string;
    emailAccount?: string;
  };
  type BaseResponseImageVo = {
    code?: number;
    data?: ImageVo;
    message?: string;
  };
  type ImageVo = {
    name?: string;
    status?: string;
    uid?: string;
    url?: string;
  };
  type ProductInfoAddRequest = {
    addPoints?: number;
    description?: string;
    expirationTime?: string;
    name?: string;
    productType?: string;
    total?: number;
  };
  type getProductInfoByIdUsingGETParams = {
    /** id */
    id?: number;
  };
  type BaseResponseProductInfo = {
    code?: number;
    data?: ProductInfo;
    message?: string;
  };
  type listProductInfoBySearchTextPageUsingGETParams = {
    current?: number;
    pageSize?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
  };
  type BaseResponsePageProductInfo = {
    code?: number;
    data?: PageProductInfo;
    message?: string;
  };
  type ProductInfoUpdateRequest = {
    addPoints?: number;
    description?: string;
    expirationTime?: string;
    id?: number;
    name?: string;
    productType?: string;
    total?: number;
  };
  type IdRequest = {
    id?: number;
  };
  type listProductInfoByPageUsingGETParams = {
    addPoints?: number;
    current?: number;
    description?: string;
    name?: string;
    pageSize?: number;
    productType?: string;
    sortField?: string;
    sortOrder?: string;
    total?: number;
  };
  type BaseResponseListProductInfo = {
    code?: number;
    data?: ProductInfo[];
    message?: string;
  };
  type listProductInfoUsingGETParams = {
    addPoints?: number;
    current?: number;
    description?: string;
    name?: string;
    pageSize?: number;
    productType?: string;
    sortField?: string;
    sortOrder?: string;
    total?: number;
  };
  type ProductInfo = {
    addPoints?: number;
    createTime?: string;
    description?: string;
    expirationTime?: string;
    id?: number;
    isDelete?: number;
    name?: string;
    productType?: string;
    status?: number;
    total?: number;
    updateTime?: string;
    userId?: number;
  };
  type closedProductOrderUsingPOSTParams = {
    /** orderNo */
    orderNo?: string;
  };
  type PayCreateRequest = {
    payType?: string;
    productId?: string;
  };
  type BaseResponseProductOrderVo = {
    code?: number;
    data?: ProductOrderVo;
    message?: string;
  };
  type deleteProductOrderUsingPOSTParams = {
    /** id */
    id?: number;
  };
  type getProductOrderByIdUsingGETParams = {
    /** id */
    id?: string;
  };
  type listProductOrderByPageUsingGETParams = {
    addPoints?: number;
    current?: number;
    orderName?: string;
    orderNo?: string;
    pageSize?: number;
    payType?: string;
    productInfo?: string;
    sortField?: string;
    sortOrder?: string;
    status?: string;
    total?: number;
  };
  type BaseResponseOrderVo = {
    code?: number;
    data?: OrderVo;
    message?: string;
  };
  type ProductOrderQueryRequest = {
    addPoints?: number;
    current?: number;
    orderName?: string;
    orderNo?: string;
    pageSize?: number;
    payType?: string;
    productInfo?: string;
    sortField?: string;
    sortOrder?: string;
    status?: string;
    total?: number;
  };
  type ProductOrderVo = {
    addPoints?: number;
    codeUrl?: string;
    createTime?: string;
    description?: string;
    expirationTime?: string;
    formData?: string;
    id?: number;
    orderName?: string;
    orderNo?: string;
    payType?: string;
    productId?: number;
    productInfo?: ProductInfo;
    productType?: string;
    status?: string;
    total?: string;
  };
  type listUserByPageUsingGETParams = {
    current?: number;
    gender?: string;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userAccount?: string;
    userName?: string;
    userRole?: string;
  };
  type BaseResponsePageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };
  type GenChatByAiRequest = {
    questionGoal?: string;
    questionName?: string;
    questionType?: string;
  };
  type BaseResponseObject_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BiResponse = {
    chartId?: number;
    genChart?: string;
    genResult?: string;
  };

}
