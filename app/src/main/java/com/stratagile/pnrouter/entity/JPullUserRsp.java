package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JPullUserRsp extends BaseEntity {


    /**
     * params : {"Action":"PullUserList","RetCode":0,"NormalUserNum":19,"TempUserNum":1,"Payload":[{"UserSN":"03F00001B827EBD4703000005B752CFA","UserType":3,"Active":0,"IdentifyCode":"","Mnemonic":"","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUqXoK7kpi6tux3nEHeqRp/J2CjjbiZ0gzfE+8CpPM1Y5SE59XhyLM6mUWZkc7L4TAk="},{"UserSN":"01000001B827EBD4703000005B752CFA","UserType":1,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"ODg4","UserId":"643ED307012B94272019C60F4D5250892D416E41A66D0AADF6A6207F0CFA96485216E718CF5D","LastLoginTime":1543979214,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUozZ2SJ/JpFnDxiS4S0UHe4jm9sl15/+GbAxHwJ+QrhEzdhNGiuYN5tm8SuCU8/nRs="},{"UserSN":"02000001B827EBD4703000005BFBDB3B","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"aG9uZ21s","UserId":"294B4CC8497EFEB47694C4D3495C2B4780F4A4DB4E2B5BEE9C73F168551F2052CBD6A5374548","LastLoginTime":1543370188,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUogBmYPHK9Qguu6pUQH8v8D9QPt7eeLpOVk6lGs4aVfGmUFS1M+UIA+caB9O2KD3hA="},{"UserSN":"02000002B827EBD4703000005BFBDC3C","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"Y2FvNA==","UserId":"1150238BD76D87E95A473FCF6F0CF8C84F3623A959E5D2B5288B066B7BE405505D105A02209A","LastLoginTime":1543370416,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUrjarJx9b1FrRoSFVRZSf6655pxjhWXUkCd/AT9Z2CG0L3/jQgp9dMt8SmrH5WLoHg="},{"UserSN":"02000003B827EBD4703000005BFCA968","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"5b+X5p2w","UserId":"C8BD548B45D521EAA71933005F26EA2B10F964E50889732C8F3F7B2089FE261E70026B7E9627","LastLoginTime":1543370755,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpbBQRijbedheMDtTXcZEo/lNUxPl5JGrDxuv6yZGb4LvJr++AVo6QF3hovTlO4MHI="},{"UserSN":"02000004B827EBD4703000005BFCABB4","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"bGljaGFv","UserId":"F13DD7DD7FCD5E0B4D93C648EE990F485D87C83969A4E2ED18221CC432186902D425D9C921CB","LastLoginTime":1543301138,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUogI8328qLuZv5751defMk19/BWnYawiLY5xVV0RSNd+YnhSc9m0dwhTHpQH8zzqf8="},{"UserSN":"02000005B827EBD4703000005BFCAE01","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"Y2FvNA==","UserId":"864FFF5355071DF05953B4093FBB5F1E0ADDE3E30655A44C92A9FCCB63DC604B7AD51413F400","LastLoginTime":1543591444,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUo2OyvKoV30IVyEIDifvOeurB4clUuidQlMQ2WjJU24geStesiPd1NqRRZxxgxddxw="},{"UserSN":"02000006B827EBD4703000005BFDF5E8","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"d2Vp","UserId":"35D975256590B9D9CE4A86695F6ACABD9120C312FCC9C5B4080A36167E2AC25774EA065BDADE","LastLoginTime":1543372394,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUouTcX56btivYp3BF2njY9chpYHQ8EI9yJ0Dk19L7sJdoNcaKa6Zm7DSNVMTzYENhU="},{"UserSN":"02000007B827EBD4703000005BFDF986","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"cmVkcmljZQ==","UserId":"5C8809E3FF5A7775BC7618AED524F85FD64BD8B650AF4266D9C6B6881519E92B6383886A3046","LastLoginTime":1543371873,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUrYeU3HH7Ys/3eKUmDnDIEpmcr5v7pLqQZNhBJVIGS0hJBAYqSUH/EdaOG8cft1xDo="},{"UserSN":"02000008B827EBD4703000005BFDFABC","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"S1pI","UserId":"7E510F5BC8EFBC4FED0B0D7BC06AE430CAED8F59D0A5D67DD46B9FC1431FFB12BA111079DB23","LastLoginTime":1543893781,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoSDiMal4J54sisRDyMgnr+PfFGEpc3SkoJDHWK/ZKb2G0lOI/8WajHTwR9TWyMXFM="},{"UserSN":"02000009B827EBD4703000005BFE1037","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"5b+X5p2w","UserId":"13640DD21DD07FABA45CE5D556C828DD21B27106F4447D1DE9E5C78690CDD00268013B28A739","LastLoginTime":1543414383,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoyivf3CysPRT4Rg48tneDEmqo0+QuAvkHhtxAZRJ0XThuFKdl56w+a8/sbWBqsMDk="},{"UserSN":"0200000AB827EBD4703000005BFE2B09","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"5pu56ICB5p2/","UserId":"0093001F79860788D17162960FB1F3C19F587F7934B50B71D91E9F45EE8B8F66A8001C937DD5","LastLoginTime":1543383898,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUq7/gyx8WLC4IcFbRt07+df9jd0IZchp+JMfy4YuKn3dLbZvkqucQfOe06S00sHAFI="},{"UserSN":"0200000BB827EBD4703000005BFE717E","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"bGM=","UserId":"1391D49ECB7C857FC6A73DCFD19BAF16E493FCFC68B135A0B9B7DCBA02B52529497F9065D21C","LastLoginTime":1543405634,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpL2b6LrI8fgaL3FggVZi2dNcZyRMGdXY9cNhqqwvnX3bhMgIfMtd1RbAamoz3xruY="},{"UserSN":"0200000CB827EBD4703000005BFE736E","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"bGMy","UserId":"C73670CB143B3C313FC0D5C7B719B540DD51B701A113252487D9C460EC81B97CAC78D456F7A4","LastLoginTime":1543402424,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpHcCRSJ4PCH9hQqEGdn9A0SqKi7J9WKdG4dTwltQYF4xUF2GAw4Pz/QQlEV3flel4="},{"UserSN":"0200000DB827EBD4703000005BFFAB61","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"WmhpamllLUNhbw==","UserId":"14C4674304097058720D6487C6588679A373036FCACBB595DE35573DCC7E090FF58DBC6C8BA7","LastLoginTime":1543590508,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUrsAShd/1mrymraBlUjqC/QBRFSJo4aKdC/wO64CpWx/e9QbtlMja7/Y47LwkE0f9s="},{"UserSN":"0200000EB827EBD4703000005C014FF0","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"V3BjYW8=","UserId":"7185370AD84515032DD743C8461BE1A349CBB99F1B89AEC9F1E89F3D7A2726173CE4F8D2F1A9","LastLoginTime":1544082178,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUqo+lXGQ9NlnrAWlfpp9dCx4QqDbXu9UcvGEDK6W0gZXw3ofudThyGIqI0byWUt5n8="},{"UserSN":"03F00001B827EBD4703000005B752CFA","UserType":3,"Active":1,"IdentifyCode":"11111111","Mnemonic":"","NickName":"tmpuser1","UserId":"4746980E4DCA5D2D52F2C2E99D69F322F5C53950859001252CDFED292CC1FA4C5F990DD73CEA","LastLoginTime":1543828560,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUqXoK7kpi6tux3nEHeqRp/J2CjjbiZ0gzfE+8CpPM1Y5SE59XhyLM6mUWZkc7L4TAk="},{"UserSN":"0200000FB827EBD4703000005C05F7BE","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"dG94MQ==","UserId":"9A19D8011C6E9DBB5A78E997699722595646892FA2E8E99DCDDAC672DB303326B9F1B21B47A4","LastLoginTime":1544084699,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpqFhBTjkwvepOeBfZTiPRVPof0NmHRMBPv/s81HWMoIpw/AIStw2dNXtpK2rrPTAU="},{"UserSN":"01000010B827EBD4703000005C0612AE","UserType":1,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"S1pIMQ==","UserId":"CAE85769D6148FBC0EBA7120DDC27CD5ED35F4773A847B8ACDE5FCAB5AC1637E03C935617CB1","LastLoginTime":1544084665,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUriNwiD5619qIW1FlVQTawY2swVns5++MI8nFmhvy37YdYjH1DVcEXMIRR0VhXf5nY="},{"UserSN":"02000011B827EBD4703000005C06130D","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"S1pILVBU","UserId":"CE7F46ED354182A3D1CE24E9E1DD1A341F076ABCC65C9313642F8B55F05E3B024CD16ED7176A","LastLoginTime":1544083896,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoohW2FU6TTFIdNMlDYtI/xwK4mCw7JHjtei/kqd3MQHCnZGTCOxI+WPXrFpjsvqKo="},{"UserSN":"02000011B827EBD4703000005C062ED8","UserType":2,"Active":0,"IdentifyCode":"123abc22","Mnemonic":"cao4","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoohW2FU6TTFIdNMlDYtI/xa5h+WOjqrudJxXbL274zxyksLeNmBTEmSlIf/J+46p0="},{"UserSN":"02000012B827EBD4703000005C062F6B","UserType":2,"Active":0,"IdentifyCode":"qwet1234","Mnemonic":"5pmu6YCa5LiA","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpSVXhmpAXB1kF6hUfhpzG/Fb4b67ZpMTnuoJjiXjVLeeyaSWQIRlMIL+oNtxunMfk="},{"UserSN":"02000013B827EBD4703000005C076B24","UserType":2,"Active":0,"IdentifyCode":"wwww1111","Mnemonic":"UFQy","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUp9mPr18cpoUZRkXQb/tFdDlVe+OjM03h7Q0EK10hJJnnLZPqYbeEcTHsDXWQtzx/g="},{"UserSN":"01000014B827EBD4703000005C08934E","UserType":1,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"dG94bWFpbg==","UserId":"1D387F0EC2A0E51CE249F602F6CDD5A46197C38C901CFEC1CC6D30FA01CEB52F9CCB4E1D24CE","LastLoginTime":1544067231,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpwvIbxEyE5yTrLwsQptHlVLE6MKDARdVffJcz5syUMk7JVOScgolYDvFiv67e+lUI="}]}
     */

    private ParamsBean params;
    private String timestampX;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public String getTimestampX() {
        return timestampX;
    }

    public void setTimestampX(String timestampX) {
        this.timestampX = timestampX;
    }

    public static class ParamsBean {
        /**
         * Action : PullUserList
         * RetCode : 0
         * NormalUserNum : 19
         * TempUserNum : 1
         * Payload : [{"UserSN":"03F00001B827EBD4703000005B752CFA","UserType":3,"Active":0,"IdentifyCode":"","Mnemonic":"","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUqXoK7kpi6tux3nEHeqRp/J2CjjbiZ0gzfE+8CpPM1Y5SE59XhyLM6mUWZkc7L4TAk="},{"UserSN":"01000001B827EBD4703000005B752CFA","UserType":1,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"ODg4","UserId":"643ED307012B94272019C60F4D5250892D416E41A66D0AADF6A6207F0CFA96485216E718CF5D","LastLoginTime":1543979214,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUozZ2SJ/JpFnDxiS4S0UHe4jm9sl15/+GbAxHwJ+QrhEzdhNGiuYN5tm8SuCU8/nRs="},{"UserSN":"02000001B827EBD4703000005BFBDB3B","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"aG9uZ21s","UserId":"294B4CC8497EFEB47694C4D3495C2B4780F4A4DB4E2B5BEE9C73F168551F2052CBD6A5374548","LastLoginTime":1543370188,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUogBmYPHK9Qguu6pUQH8v8D9QPt7eeLpOVk6lGs4aVfGmUFS1M+UIA+caB9O2KD3hA="},{"UserSN":"02000002B827EBD4703000005BFBDC3C","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"Y2FvNA==","UserId":"1150238BD76D87E95A473FCF6F0CF8C84F3623A959E5D2B5288B066B7BE405505D105A02209A","LastLoginTime":1543370416,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUrjarJx9b1FrRoSFVRZSf6655pxjhWXUkCd/AT9Z2CG0L3/jQgp9dMt8SmrH5WLoHg="},{"UserSN":"02000003B827EBD4703000005BFCA968","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"5b+X5p2w","UserId":"C8BD548B45D521EAA71933005F26EA2B10F964E50889732C8F3F7B2089FE261E70026B7E9627","LastLoginTime":1543370755,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpbBQRijbedheMDtTXcZEo/lNUxPl5JGrDxuv6yZGb4LvJr++AVo6QF3hovTlO4MHI="},{"UserSN":"02000004B827EBD4703000005BFCABB4","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"bGljaGFv","UserId":"F13DD7DD7FCD5E0B4D93C648EE990F485D87C83969A4E2ED18221CC432186902D425D9C921CB","LastLoginTime":1543301138,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUogI8328qLuZv5751defMk19/BWnYawiLY5xVV0RSNd+YnhSc9m0dwhTHpQH8zzqf8="},{"UserSN":"02000005B827EBD4703000005BFCAE01","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"Y2FvNA==","UserId":"864FFF5355071DF05953B4093FBB5F1E0ADDE3E30655A44C92A9FCCB63DC604B7AD51413F400","LastLoginTime":1543591444,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUo2OyvKoV30IVyEIDifvOeurB4clUuidQlMQ2WjJU24geStesiPd1NqRRZxxgxddxw="},{"UserSN":"02000006B827EBD4703000005BFDF5E8","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"d2Vp","UserId":"35D975256590B9D9CE4A86695F6ACABD9120C312FCC9C5B4080A36167E2AC25774EA065BDADE","LastLoginTime":1543372394,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUouTcX56btivYp3BF2njY9chpYHQ8EI9yJ0Dk19L7sJdoNcaKa6Zm7DSNVMTzYENhU="},{"UserSN":"02000007B827EBD4703000005BFDF986","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"cmVkcmljZQ==","UserId":"5C8809E3FF5A7775BC7618AED524F85FD64BD8B650AF4266D9C6B6881519E92B6383886A3046","LastLoginTime":1543371873,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUrYeU3HH7Ys/3eKUmDnDIEpmcr5v7pLqQZNhBJVIGS0hJBAYqSUH/EdaOG8cft1xDo="},{"UserSN":"02000008B827EBD4703000005BFDFABC","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"S1pI","UserId":"7E510F5BC8EFBC4FED0B0D7BC06AE430CAED8F59D0A5D67DD46B9FC1431FFB12BA111079DB23","LastLoginTime":1543893781,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoSDiMal4J54sisRDyMgnr+PfFGEpc3SkoJDHWK/ZKb2G0lOI/8WajHTwR9TWyMXFM="},{"UserSN":"02000009B827EBD4703000005BFE1037","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"5b+X5p2w","UserId":"13640DD21DD07FABA45CE5D556C828DD21B27106F4447D1DE9E5C78690CDD00268013B28A739","LastLoginTime":1543414383,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoyivf3CysPRT4Rg48tneDEmqo0+QuAvkHhtxAZRJ0XThuFKdl56w+a8/sbWBqsMDk="},{"UserSN":"0200000AB827EBD4703000005BFE2B09","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"5pu56ICB5p2/","UserId":"0093001F79860788D17162960FB1F3C19F587F7934B50B71D91E9F45EE8B8F66A8001C937DD5","LastLoginTime":1543383898,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUq7/gyx8WLC4IcFbRt07+df9jd0IZchp+JMfy4YuKn3dLbZvkqucQfOe06S00sHAFI="},{"UserSN":"0200000BB827EBD4703000005BFE717E","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"bGM=","UserId":"1391D49ECB7C857FC6A73DCFD19BAF16E493FCFC68B135A0B9B7DCBA02B52529497F9065D21C","LastLoginTime":1543405634,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpL2b6LrI8fgaL3FggVZi2dNcZyRMGdXY9cNhqqwvnX3bhMgIfMtd1RbAamoz3xruY="},{"UserSN":"0200000CB827EBD4703000005BFE736E","UserType":2,"Active":1,"IdentifyCode":"111111","Mnemonic":"","NickName":"bGMy","UserId":"C73670CB143B3C313FC0D5C7B719B540DD51B701A113252487D9C460EC81B97CAC78D456F7A4","LastLoginTime":1543402424,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpHcCRSJ4PCH9hQqEGdn9A0SqKi7J9WKdG4dTwltQYF4xUF2GAw4Pz/QQlEV3flel4="},{"UserSN":"0200000DB827EBD4703000005BFFAB61","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"WmhpamllLUNhbw==","UserId":"14C4674304097058720D6487C6588679A373036FCACBB595DE35573DCC7E090FF58DBC6C8BA7","LastLoginTime":1543590508,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUrsAShd/1mrymraBlUjqC/QBRFSJo4aKdC/wO64CpWx/e9QbtlMja7/Y47LwkE0f9s="},{"UserSN":"0200000EB827EBD4703000005C014FF0","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"V3BjYW8=","UserId":"7185370AD84515032DD743C8461BE1A349CBB99F1B89AEC9F1E89F3D7A2726173CE4F8D2F1A9","LastLoginTime":1544082178,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUqo+lXGQ9NlnrAWlfpp9dCx4QqDbXu9UcvGEDK6W0gZXw3ofudThyGIqI0byWUt5n8="},{"UserSN":"03F00001B827EBD4703000005B752CFA","UserType":3,"Active":1,"IdentifyCode":"11111111","Mnemonic":"","NickName":"tmpuser1","UserId":"4746980E4DCA5D2D52F2C2E99D69F322F5C53950859001252CDFED292CC1FA4C5F990DD73CEA","LastLoginTime":1543828560,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUqXoK7kpi6tux3nEHeqRp/J2CjjbiZ0gzfE+8CpPM1Y5SE59XhyLM6mUWZkc7L4TAk="},{"UserSN":"0200000FB827EBD4703000005C05F7BE","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"dG94MQ==","UserId":"9A19D8011C6E9DBB5A78E997699722595646892FA2E8E99DCDDAC672DB303326B9F1B21B47A4","LastLoginTime":1544084699,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpqFhBTjkwvepOeBfZTiPRVPof0NmHRMBPv/s81HWMoIpw/AIStw2dNXtpK2rrPTAU="},{"UserSN":"01000010B827EBD4703000005C0612AE","UserType":1,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"S1pIMQ==","UserId":"CAE85769D6148FBC0EBA7120DDC27CD5ED35F4773A847B8ACDE5FCAB5AC1637E03C935617CB1","LastLoginTime":1544084665,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUriNwiD5619qIW1FlVQTawY2swVns5++MI8nFmhvy37YdYjH1DVcEXMIRR0VhXf5nY="},{"UserSN":"02000011B827EBD4703000005C06130D","UserType":2,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"S1pILVBU","UserId":"CE7F46ED354182A3D1CE24E9E1DD1A341F076ABCC65C9313642F8B55F05E3B024CD16ED7176A","LastLoginTime":1544083896,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoohW2FU6TTFIdNMlDYtI/xwK4mCw7JHjtei/kqd3MQHCnZGTCOxI+WPXrFpjsvqKo="},{"UserSN":"02000011B827EBD4703000005C062ED8","UserType":2,"Active":0,"IdentifyCode":"123abc22","Mnemonic":"cao4","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUoohW2FU6TTFIdNMlDYtI/xa5h+WOjqrudJxXbL274zxyksLeNmBTEmSlIf/J+46p0="},{"UserSN":"02000012B827EBD4703000005C062F6B","UserType":2,"Active":0,"IdentifyCode":"qwet1234","Mnemonic":"5pmu6YCa5LiA","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpSVXhmpAXB1kF6hUfhpzG/Fb4b67ZpMTnuoJjiXjVLeeyaSWQIRlMIL+oNtxunMfk="},{"UserSN":"02000013B827EBD4703000005C076B24","UserType":2,"Active":0,"IdentifyCode":"wwww1111","Mnemonic":"UFQy","NickName":"","UserId":"","LastLoginTime":0,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUp9mPr18cpoUZRkXQb/tFdDlVe+OjM03h7Q0EK10hJJnnLZPqYbeEcTHsDXWQtzx/g="},{"UserSN":"01000014B827EBD4703000005C08934E","UserType":1,"Active":1,"IdentifyCode":"QLCADMIN","Mnemonic":"","NickName":"dG94bWFpbg==","UserId":"1D387F0EC2A0E51CE249F602F6CDD5A46197C38C901CFEC1CC6D30FA01CEB52F9CCB4E1D24CE","LastLoginTime":1544067231,"Qrcode":"MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUpwvIbxEyE5yTrLwsQptHlVLE6MKDARdVffJcz5syUMk7JVOScgolYDvFiv67e+lUI="}]
         */

        private String Action;
        private int RetCode;
        private int NormalUserNum;
        private int TempUserNum;
        private List<PayloadBean> Payload;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int RetCode) {
            this.RetCode = RetCode;
        }

        public int getNormalUserNum() {
            return NormalUserNum;
        }

        public void setNormalUserNum(int NormalUserNum) {
            this.NormalUserNum = NormalUserNum;
        }

        public int getTempUserNum() {
            return TempUserNum;
        }

        public void setTempUserNum(int TempUserNum) {
            this.TempUserNum = TempUserNum;
        }

        public List<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(List<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean {
            /**
             * UserSN : 03F00001B827EBD4703000005B752CFA
             * UserType : 3
             * Active : 0
             * IdentifyCode :
             * Mnemonic :
             * NickName :
             * UserId :
             * LastLoginTime : 0
             * Qrcode : MVmz1Y+j7E6XXm98gH71ZKxrmLXDeC+HeexGtqnYZTlz8GlRZTWOYsQzOlOzBY1Zs5golCeA2wZoQqhG64II5cUv5zc75RwZQZSXsZgChUqXoK7kpi6tux3nEHeqRp/J2CjjbiZ0gzfE+8CpPM1Y5SE59XhyLM6mUWZkc7L4TAk=
             */

            private String UserSN;
            private int UserType;
            private int Active;
            private String IdentifyCode;
            private String Mnemonic;
            private String NickName;
            private String UserId;
            private int LastLoginTime;
            private String Qrcode;

            public String getUserSN() {
                return UserSN;
            }

            public void setUserSN(String UserSN) {
                this.UserSN = UserSN;
            }

            public int getUserType() {
                return UserType;
            }

            public void setUserType(int UserType) {
                this.UserType = UserType;
            }

            public int getActive() {
                return Active;
            }

            public void setActive(int Active) {
                this.Active = Active;
            }

            public String getIdentifyCode() {
                return IdentifyCode;
            }

            public void setIdentifyCode(String IdentifyCode) {
                this.IdentifyCode = IdentifyCode;
            }

            public String getMnemonic() {
                return Mnemonic;
            }

            public void setMnemonic(String Mnemonic) {
                this.Mnemonic = Mnemonic;
            }

            public String getNickName() {
                return NickName;
            }

            public void setNickName(String NickName) {
                this.NickName = NickName;
            }

            public String getUserId() {
                return UserId;
            }

            public void setUserId(String UserId) {
                this.UserId = UserId;
            }

            public int getLastLoginTime() {
                return LastLoginTime;
            }

            public void setLastLoginTime(int LastLoginTime) {
                this.LastLoginTime = LastLoginTime;
            }

            public String getQrcode() {
                return Qrcode;
            }

            public void setQrcode(String Qrcode) {
                this.Qrcode = Qrcode;
            }
        }
    }
}
