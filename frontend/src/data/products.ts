export interface Product {
  id: number;
  name: string;
  price: number;
  image: string;
  stock: number; 
  status: "ACTIVE" | "INACTIVE"; 
}

export const dummyProducts: Product[] = [
  {
    id: 1,
    name: "Vợt Pickleball Kuikma Open Xanh Dương Kuikma",
    price: 699000,
    image: "https://contents.mediadecathlon.com/p2826038/sq/k$e4041aee2387734fca74301b29dde496/v%E1%BB%A3t-pickleball-kuikma-open-xanh-d%C6%B0%C6%A1ng-kuikma-8941064.jpg?f=480x480&format=auto",
    stock: 10,
    status: "INACTIVE"
  },
  {
    id: 2,
    name: "Vợt Pickleball Franklin FS Tour Dynasty 16mm Franklin",
    price: 2999000,
    image: "https://contents.mediadecathlon.com/p3115740/sq/k$3945ae1f148597e77555630547754eba/v%E1%BB%A3t-pickleball-franklin-fs-tour-dynasty-16mm-franklin-9026862.jpg?f=480x480&format=auto",
    stock: 0,
    status: "ACTIVE"
  },
  {
    id: 3,
    name: "Vợt Pickleball Open Đỏ Đen Kuikma",
    price: 699000,
    image: "https://contents.mediadecathlon.com/p3081933/sq/k$bb3e04053aab61dac119bd988d7a4c9d/v%E1%BB%A3t-pickleball-open-%C4%91%E1%BB%8F-%C4%91en-kuikma-9007787.jpg?f=480x480&format=auto",
    stock: 15,
    status: "ACTIVE"
  },
  {
    id: 4,
    name: "Vợt Pickleball Candy Melody 16mm Hồng Facolos",
    price: 799000,
    image: "https://contents.mediadecathlon.com/p3058563/sq/k$5f9d4b32095422a53f95096e53539fa7/v%E1%BB%A3t-pickleball-candy-melody-16mm-h%E1%BB%93ng-facolos-9032369.jpg?f=480x480&format=auto",
    stock: 8,
    status: "ACTIVE"
  },
  {
    id: 5,
    name: "Ghế Tựa Cắm Trại 5 Tư Thế",
    price: 1799000,
    image: "https://contents.mediadecathlon.com/p2817292/sq/k$5cad43d70b18bd67f22fa34648faa847/gh%E1%BA%BF-t%E1%BB%B1a-c%E1%BA%AFm-tr%E1%BA%A1i-5-t%C6%B0-th%E1%BA%BF-tho%E1%BA%A3i-m%C3%A1i-v%C3%A0-nh%E1%BB%8F-g%E1%BB%8Dn-m%C3%A0u-be-quechua-8901540.jpg?f=480x480&format=auto",
    stock: 12,
    status: "ACTIVE"
  },
  {
    id: 6,
    name: "Lều Cắm Trại 2 Người",
    price: 699000,
    image: "https://contents.mediadecathlon.com/p2579082/sq/k$684efb64d5468fb773577cdf1cb57a08/l%E1%BB%81u-c%E1%BA%AFm-tr%E1%BA%A1i-2-ng%C6%B0%E1%BB%9Di-mh100-x%C3%A1m-quechua-8513471.jpg?f=480x480&format=auto",
    stock: 20,
    status: "ACTIVE"
  },
  {
    id: 7,
    name: "Áo Khoác Dã Ngoại Nam Chống Tia UV",
    price: 899000,
    image: "https://contents.mediadecathlon.com/p2958658/sq/k$ec959a5cdb2080e552cac9ec9e99c638/%C3%A1o-kho%C3%A1c-d%C3%A3-ngo%E1%BA%A1i-nam-ch%E1%BB%91ng-tia-uv-knit-500-x%C3%A1m-quechua-8929877.jpg?f=480x480&format=auto",
    stock: 25,
    status: "ACTIVE"
  },
  {
    id: 8,
    name: "Áo Khoác Chống Nắng Nữ",
    price: 799000,
    image: "https://contents.mediadecathlon.com/p2854711/sq/k$b3b616c03e2da628d601de0683070ae5/%C3%A1o-kho%C3%A1c-ch%E1%BB%91ng-n%E1%BA%AFng-n%E1%BB%AF-upf50-g%E1%BA%A5p-g%E1%BB%8Dn-leo-n%C3%BAi-du-l%E1%BB%8Bch-helium-500-xanh-d%C6%B0%C6%A1ng-quechua-8873277.jpg?f=480x480&format=auto",
    stock: 18,
    status: "ACTIVE"
  }
];
