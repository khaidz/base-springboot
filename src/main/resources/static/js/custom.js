
$(document).ready(function () {
    $('#load').hide()
    $('#lang-en').add('#lang-vi').on('click', function () {
        let lang = $(this).data('lang');
        $(this).attr('href', getUrlChangeLanguage(lang));
    })

    loadDataProvinceDistrict();
});
function trimToNull(text){
    if (text === null || text === undefined) return null;
    let temp = text.trim();
    if (temp.length === 0) return null;
    return temp;
}

function formatData(data = {}){
    for (let key of Object.keys(data)){
        if (typeof data[key] === 'string'){
            data[key] = trimToNull(data[key])
        }
    }
    return data;
}

function getQueryParam(parameterName) {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get(parameterName);
}

function urlSearchParamsToText(searchParams) {
    let text = '';
    for (const [key, value] of searchParams) {
        text += `${key}=${encodeURIComponent(value)}&`;
    }
    // Xóa ký tự '&' cuối cùng nếu có
    return text.slice(0, -1);
}

function getUrlChangeLanguage(lang) {
    const queryString = window.location.search;
    let searchParams = new URLSearchParams(queryString);
    if (searchParams.size === 0 || searchParams.get("lang") === null){
        searchParams.append("lang", lang);
    } else {
        searchParams.set("lang", lang);
    }

    let url = location.origin + location.pathname + '?' + urlSearchParamsToText(searchParams);
    return url;
}

function showLoading(flag){
    let value = flag ? "block": "none";
    $("#load").css("display", value);
}

function showAlert(type = 'error', heading= 'Error', message = 'Có lỗi xảy ra. Vui lòng thử lại sau'){
    //type = ['success', 'error', 'warning', 'info'];
    $.toast({
        heading: heading,
        text: message,
        position: 'top-right',
        hideAfter: 2000,
        allowToastClose: false,
        stack: 4,
        icon: type
    })
}
function loadDataProvinceDistrict(){
    let flagProvince = localStorage.getItem("flagProvince");
    let flagDistrict = localStorage.getItem("flagDistrict");
    let dropdownProvince = localStorage.getItem("dropdown.province");
    if (!dropdownProvince || flagProvince !== "0"){
        $.ajax({
            url: '/api/dropdown/province',
            dataType: 'json',
            cache: false,
            success: function(result, status,xhr){
                if (xhr.status === 200){
                    localStorage.setItem("dropdown.province", JSON.stringify(result))
                    localStorage.setItem("flagProvince", "0")
                }
            },
            error: function(xhr,status,error){
                console.error('error: ', error)
            }
        })
    }

    let dropdownDistrict = localStorage.getItem("dropdown.district");
    if (!dropdownDistrict || flagDistrict !== "0"){
        $.ajax({
            url: '/api/dropdown/district',
            dataType: 'json',
            cache: false,
            success: function(result, status,xhr){
                if (xhr.status === 200){
                    localStorage.setItem("dropdown.district", JSON.stringify(result))
                    localStorage.setItem("flagDistrict", "0")
                }
            },
            error: function(xhr,status,error){
                console.error('error: ', error)
            }
        })
    }
}
function getValueSelect2(selector){
    let data = $(selector).select2('data');
    if (!data || data.length === 0) return '';
    return data[0].id || '';
}